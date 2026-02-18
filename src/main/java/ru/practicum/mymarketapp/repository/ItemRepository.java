package ru.practicum.mymarketapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mymarketapp.entity.Item;
@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
}
