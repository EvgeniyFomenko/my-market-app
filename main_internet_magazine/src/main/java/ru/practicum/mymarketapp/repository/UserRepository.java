package ru.practicum.mymarketapp.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.User;

public interface UserRepository extends R2dbcRepository<User,Long> {
    Mono<User> getUserByLogin(String username);
}
