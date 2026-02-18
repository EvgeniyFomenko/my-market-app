package ru.practicum.mymarketapp.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;

@DataJpaTest
public class OrderRepositoryTest {
    @Autowired
    OrderRepository orderRepository;
    Order order;

    @BeforeEach
    public void init(){
        order = new Order();
        order.setPaid(false);
        order.setTotal(new BigDecimal(100));
    }
    @Test
    public void saveOrder(){
       Order newOrder = orderRepository.save(order);
        Assertions.assertNotNull(newOrder.getId());
    }

    @Test
    public void deleteOrder() {
        Order newOrder = orderRepository.save(order);
        long id = newOrder.getId();
        orderRepository.deleteById(id);
        Order findOrder = orderRepository.findById(id).orElse(null);
        Assertions.assertNull(findOrder);
    }

    @Test
    public void findOrderByid() {
        Order newOrder = orderRepository.save(order);
        long id = newOrder.getId();
        Order findOrder = orderRepository.findById(id).orElse(null);
        Assertions.assertNotNull(findOrder);
    }
}
