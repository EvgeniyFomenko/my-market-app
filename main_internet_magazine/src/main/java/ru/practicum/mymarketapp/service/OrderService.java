package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.pojo.Action;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }
    public Mono<Order> findNewOrderOrTakeNew() {
        return orderRepository.findByIsPaidFalse().take(1)
                .singleOrEmpty()
                .switchIfEmpty(Mono.fromSupplier( ()-> {
            Order orderNew = new Order();
            orderNew.setPaid(false);
            orderNew.setTotal(new BigDecimal(0));
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

    public Flux<Order> findPaidOrdersIsPaidTrue() {
        return orderRepository.findByIsPaidTrue();
    }

    public void updatePaid(Order order) {
        order.setPaid(true);
        orderRepository.save(order).subscribe();
    }

    public Mono<Order> findOrderById(Long id) {
        return orderRepository.findById(id).doOnError((e)-> {throw new RuntimeException(String.format("Карзина c id: %s не найдена",id));});
    }

    public Mono<Order> changePriceOrderByActionOnCartItemCount( String action, CartItemCount cartItemCount){
       return orderRepository.findById(cartItemCount.getOrderId())
               .zipWith(itemRepository.findById(cartItemCount.getItemId())).map( tuple2 -> {
            BigDecimal total = null; //Обновляем стоимость заказа
                   Order order = tuple2.getT1();
                   Item item = tuple2.getT2();
            if (Action.PLUS.getFullName().equals(action)) {
                total = order.getTotal().add(new BigDecimal(item.getPrice()));
            }
            else if (Action.MINUS.getFullName().equals(action)) {
                total = order.getTotal().subtract(new BigDecimal(item.getPrice()));
            }
            else  if (Action.DELETE.getFullName().equals(action)) {
                total = order.getTotal();
                if (cartItemCount.getQuantity() == 0){
                    total = total.subtract(new BigDecimal(item.getPrice()));
                } else {
                    total = total.subtract(new BigDecimal(item.getPrice() * cartItemCount.getQuantity()));
                }
            }

            if (total.compareTo(new BigDecimal(0)) == 0 ) {
                 delete(order).subscribe();
                 return Mono.just(order);
            } else {
                order.setTotal(total);
              return  orderRepository.save(order);
            }
                }
        ).flatMap(e->e);
    }
}
