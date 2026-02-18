package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.util.List;
@Service
public class OrderService {
    private OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public Order findNewOrder() {
        List<Order> orders = orderRepository.findByIsPaidFalse();
        if (orders.isEmpty()) {
            return null;
        }
        Order order = orders.stream().filter(o -> !o.isPaid()).findFirst().orElse(null);
        return order;
    }

    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    public void delete(Order order) {
        orderRepository.delete(order);
    }

    public List<Order> findPaidOrdersIsPaidTrue() {
        return orderRepository.findByIsPaidTrue();
    }

    public void updatePaid(Order order) {
        order.setPaid(true);
        orderRepository.save(order);
    }

    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }
}
