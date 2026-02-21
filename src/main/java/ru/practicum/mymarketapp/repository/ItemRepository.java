package ru.practicum.mymarketapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mymarketapp.entity.Item;
@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    Page<Item> findItemByTitle(String search, Pageable pageable);
}
