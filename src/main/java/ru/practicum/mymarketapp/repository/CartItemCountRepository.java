package ru.practicum.mymarketapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;


import java.util.List;
@Repository
public interface CartItemCountRepository extends JpaRepository<CartItemCount,Long> {
    List<CartItemCount> findByOrderId(Order orderId);
    CartItemCount  findByItemIdAndOrderId( Item itemId, Order orderId);
}
