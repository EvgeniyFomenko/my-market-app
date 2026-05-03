package ru.practicum.mymarketapp.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;


import java.util.List;
@Repository
public interface CartItemCountRepository extends R2dbcRepository<CartItemCount,Long> {
    Flux<CartItemCount> findByOrderId(Long orderId);
    Mono<CartItemCount> findByItemIdAndOrderId(Long itemId, Long orderId);
    @Query("UPDATE cart_item_count set quantity = ?2 where id = ?1")
    Mono<CartItemCount> update(Long id, int quantity);
}
