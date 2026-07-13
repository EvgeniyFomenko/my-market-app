package ru.practicum.mymarketapp.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.*;
import ru.practicum.mymarketapp.entity.Balance;
import ru.practicum.mymarketapp.entity.Quantity;
import ru.practicum.mymarketapp.pojo.Action;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;


import java.math.BigDecimal;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ru.practicum.mymarketapp.service.DefaultApi defaultApi;
    private final CarUserService carUserService;
    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, DefaultApi defaultApi, CarUserService carUserService, ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.defaultApi = defaultApi;
        this.carUserService = carUserService;
        this.authorizedClientManager = authorizedClientManager;
    }

    public Mono<Order> findNewOrderOrTakeNewByUserLoginOrNew(String userLogin) {

        return   this.findNewOrderOrTakeNewByUserLogin(userLogin)
                .flatMap(order -> carUserService.setOrder(userLogin, order))
                .flatMap(cartUser-> orderRepository.findById(cartUser.getCartId()));
    }


    private Mono<Order> findNewOrderOrTakeNewByUserLogin(String userLogin) {
        return  carUserService.getCartUserByUserLogin(userLogin)
                .flatMap(e-> orderRepository.findById(e.getCartId()))
                .filter(order->!order.isPaid())
                .singleOrEmpty().switchIfEmpty(Mono.fromSupplier(() -> {
                    Order orderNew = new Order();
                    orderNew.setPaid(false);
                    orderNew.setTotal(BigDecimal.ZERO);
                    return orderNew;
                }).flatMap(orderRepository::save));
    }

    public Mono<Order> findNewOrderOrTakeNew() {
        return orderRepository.findByIsPaidFalse().take(1)
                .singleOrEmpty()
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    Order orderNew = new Order();
                    orderNew.setPaid(false);
                    orderNew.setTotal(BigDecimal.ZERO);
                    return orderNew;
                }).flatMap(orderRepository::save));
    }

    public Mono<Order> findNewOrder() {
        return orderRepository.findByIsPaidFalse().take(1)
                .singleOrEmpty();
    }

    public Mono<Order> addOrder(Order order) {
        return orderRepository.save(order);
    }

    public Mono<Void> delete(Order order) {
        return orderRepository.delete(order);
    }

    public Mono<Void> deleteById(long order) {
        return orderRepository.findById(order).flatMap(e->  orderRepository.delete(e).then());
    }


    public Flux<Order> findPaidOrdersIsPaidTrue() {
        return orderRepository.findByIsPaidTrue();
    }

    public Mono<Void> updatePaid(Order order) {
        Quantity newQuantity = new Quantity().quantity(order.getTotal().toString());
        order.setPaid(true);
        return getAuthorizedClient().doOnNext( e-> defaultApi.getApiClient().setBearerToken(e.getAccessToken().getTokenValue())).then(defaultApi.toPayPost(newQuantity)).then(orderRepository.save(order)).thenEmpty(Mono.empty()) ;

    }

    public Mono<Balance> getBalance() {
        return getAuthorizedClient().flatMap(client-> {
                    String token = client.getAccessToken().getTokenValue();
                    defaultApi.getApiClient().setBearerToken(token);
                    return defaultApi.getBalanceGet(); // запрос выполняетс
                        }
                );
    }

    public Mono<OAuth2AuthorizedClient> getAuthorizedClient() {
        return authorizedClientManager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("keycloak").principal("system").build());
    }

    public Mono<Order> findOrderById(Long id) {
        return orderRepository.findById(id).doOnError((e) -> {
            throw new RuntimeException(String.format("Карзина c id: %s не найдена", id));
        });
    }

    /**
     * Ищем заказ в статусе new или создаем такой и увеличиваем, уменьшаем или удалем его total на количество товаров в корзине
     * @param action
     * @param cartItemCount
     * @return
     */
    public Mono<Order> changePriceOrderByActionOnCartItemCount(String action, CartItemCount cartItemCount) {
        return orderRepository.findById(cartItemCount.getOrderId())
                .zipWith(itemRepository.findById(cartItemCount.getItemId())).flatMap(tuple2 -> {
                            Order order = tuple2.getT1();
                            BigDecimal total = order.getTotal();
                            Item item = tuple2.getT2();
                            BigDecimal itemPrice = new BigDecimal(item.getPrice());
                            BigDecimal itemQuantity = new BigDecimal(cartItemCount.getQuantity());

                            if (Action.PLUS.getFullName().equals(action)) {
                                total = total.add(itemPrice);
                            } else if (Action.MINUS.getFullName().equals(action)) {
                                total = total.subtract(itemPrice);
                            } else if (Action.DELETE.getFullName().equals(action)) {
                                if (itemQuantity.compareTo(BigDecimal.ZERO) == 0) {
                                    total = total.subtract(itemPrice);
                                } else {
                                    total = total.subtract(itemPrice.multiply(itemQuantity));
                                }
                            }

                            if (total.compareTo(BigDecimal.ZERO) == 0) {
                                return delete(order).then(Mono.just(order));
                            } else {
                                order.setTotal(total);
                                return orderRepository.save(order);
                            }
                        }
                );
    }
}
