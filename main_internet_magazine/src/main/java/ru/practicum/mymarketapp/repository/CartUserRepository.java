package ru.practicum.mymarketapp.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartUser;
import ru.practicum.mymarketapp.entity.Order;

public interface CartUserRepository extends R2dbcRepository<CartUser,Long> {
    Flux<CartUser> findCartUserByUserId(Long id);
    Mono<CartUser> findByCartId(Order order);
}
