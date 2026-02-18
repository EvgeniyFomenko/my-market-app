package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

@Service
public class CartItemCountService {
    private final CartItemCountRepository cartItemCountRepository;

    public CartItemCountService(CartItemCountRepository cartItemCountRepository, OrderRepository orderRepository) {
        this.cartItemCountRepository = cartItemCountRepository;
    }

    public List<CartItemCount> findAll() {
        return cartItemCountRepository.findAll();
    }

    public void save(CartItemCount cartItemCount) {
        cartItemCountRepository.save(cartItemCount);
    }

    public CartItemCount findByItemIdAndOrderId(Item itemId, Order orderId) {
       return cartItemCountRepository.findByItemIdAndOrderId(itemId, orderId);
    }

    public void delete(CartItemCount cartItemCount) {
        cartItemCountRepository.delete(cartItemCount);
    }

    public List<Item> findItemByOrderId(Order order) {
        return cartItemCountRepository.findByOrderId(order).stream().filter(e-> Objects.nonNull(e.getItemId())).peek(e-> e.getItemId().setCount(e.getQuantity())).map(CartItemCount::getItemId).toList();
    }

    public List<CartItemCount> findCartItemCountByOrderId(Order order) {
        return cartItemCountRepository.findByOrderId(order);
    }
}
