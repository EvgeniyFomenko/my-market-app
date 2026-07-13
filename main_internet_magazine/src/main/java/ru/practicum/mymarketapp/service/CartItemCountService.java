package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.pojo.Action;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;

import java.sql.SQLOutput;
import java.util.Objects;

@Service
public class CartItemCountService {
    private final CartItemCountRepository cartItemCountRepository;
    private final ItemRepository itemRepository;

    public CartItemCountService(CartItemCountRepository cartItemCountRepository, ItemRepository itemRepository) {
        this.cartItemCountRepository = cartItemCountRepository;
        this.itemRepository = itemRepository;
    }

    public Flux<CartItemCount> findAll() {
        return cartItemCountRepository.findAll();
    }

    public Mono<CartItemCount> save(CartItemCount cartItemCount) {
      return cartItemCountRepository.save(cartItemCount);
    }

    public Mono<CartItemCount> findByItemIdAndOrderId(Long itemId, Long orderId) {
        return cartItemCountRepository.findByItemIdAndOrderId(itemId, orderId);
    }

    public Mono<Void> delete(CartItemCount cartItemCount) {
        return cartItemCountRepository.delete(cartItemCount);
    }

    public Flux<Item> findItemByOrderId(Long order) {
        return cartItemCountRepository.findByOrderId(order)
                .flatMap(e ->
                    itemRepository.findById(e.getItemId()).map(item -> {item.setCount(e.getQuantity());
                    return item;})
                );

    }

    /**
     * Увеличиваем , уменьшаем или удаляем Item из корзины
     * @param cartItemCount
     * @param action
     * @return
     */
    public Mono<CartItemCount> changePriceCartByAction(CartItemCount cartItemCount, String action) {
      return Mono.just(cartItemCount)
                .flatMap(cartItemCount1->{
                    if (Action.PLUS.getFullName().equals(action) || Action.MINUS.getFullName().equals(action)) {
                        cartItemCount1.setQuantity(Action.PLUS.getFullName().equals(action) ? cartItemCount1.getQuantity() + 1 : cartItemCount1.getQuantity() - 1);
                        if (cartItemCount1.getQuantity() == 0) {
                          return  delete(cartItemCount1).then(Mono.just(cartItemCount1));
                        } else {
                           return save(cartItemCount1);
                        }
                    } else if (Action.DELETE.getFullName().equals(action)) {
                      return delete(cartItemCount1).then(Mono.just(cartItemCount1));
                    }
                    return Mono.just(cartItemCount1);
                });
    }

    public Flux<CartItemCount> findCartItemCountByOrderId(Long order) {
        return cartItemCountRepository.findByOrderId(order);
    }

    public Mono<CartItemCount> findByOrderAndItemId(Long order, Long id) {
        return findCartItemCountByOrderId(order)
                .filter(cartItemCount -> Objects.equals(cartItemCount.getItemId(), id))
                .singleOrEmpty();
    }

    public Mono<CartItemCount> createOrFindByOrderAndItemId(Long order, Long item) {
       return findByOrderAndItemId(order, item)
                        .switchIfEmpty(Mono.fromSupplier(()->{
                            CartItemCount cartItemCountNew = new CartItemCount();
                            cartItemCountNew.setItemId(item);
                            cartItemCountNew.setOrderId(order);
                            cartItemCountNew.setQuantity(0);
                            return cartItemCountNew;
                        }).flatMap(this::save));
    }
}
