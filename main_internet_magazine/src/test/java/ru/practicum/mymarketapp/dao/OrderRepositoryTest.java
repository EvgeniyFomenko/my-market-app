package ru.practicum.mymarketapp.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.mymarketapp.PostgresqlTestContainer;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresqlTestContainer.class)
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
       Order newOrder = orderRepository.save(order).block();
        Assertions.assertNotNull(newOrder);
    }

    @Test
    public void deleteOrder() {
        Order newOrder = orderRepository.save(order).block();
        long id = newOrder.getId();
        orderRepository.deleteById(id).block();
        Order findOrder = orderRepository.findById(id).block();
        Assertions.assertNull(findOrder);
    }

    @Test
    public void findOrderByid() {
        Order newOrder = orderRepository.save(order).block();
        long id = newOrder.getId();
        Order findOrder = orderRepository.findById(id).block();
        Assertions.assertNotNull(findOrder);
    }
}
