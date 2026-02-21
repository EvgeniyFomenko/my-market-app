package ru.practicum.mymarketapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mymarketapp.entity.Order;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByIsPaidTrue();
    List<Order> findByIsPaidFalse();
}
