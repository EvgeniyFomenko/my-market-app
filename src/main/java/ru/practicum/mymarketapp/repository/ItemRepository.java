package ru.practicum.mymarketapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    Flux<Item> findItemByTitle(String search, Pageable pageable);

    Flux<Item> findAllBy(Pageable pageable);
}
