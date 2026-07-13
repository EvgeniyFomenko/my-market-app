package ru.practicum.mymarketapp.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.practicum.mymarketapp.entity.UserRole;

public interface UserRoleRepository extends R2dbcRepository<UserRole,Long> {
    Flux<UserRole> findUserRoleByUserId(Long userId);
}
