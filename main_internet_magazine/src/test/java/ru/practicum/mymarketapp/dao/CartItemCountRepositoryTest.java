package ru.practicum.mymarketapp.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.PostgresqlTestContainer;
import ru.practicum.mymarketapp.RedisTestContainer;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@Testcontainers
@ImportTestcontainers( {PostgresqlTestContainer.class, RedisTestContainer.class })
public class CartItemCountRepositoryTest {
    @Autowired
    CartItemCountRepository cartItemCountRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    CartItemCount cartItemCount;
    Order order;
    Item item;

    @BeforeEach
    public void init(){
        order = new Order();
        order.setTotal(new BigDecimal(100));
        order.setPaid(false);
        item = new Item();
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Title");
        item.setDescription("Description");
        item = itemRepository.save(item).block();
        order = orderRepository.save(order).block();
        cartItemCount = new CartItemCount();
        cartItemCount.setItemId(item.getId());
        cartItemCount.setOrderId(order.getId());
    }

    @AfterEach
    public void clean(){
        cartItemCountRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();
    }
    @Test
    public void saveCartItemCount() {
        cartItemCount = cartItemCountRepository.save(cartItemCount).block();
        CartItemCount cic =  cartItemCountRepository.findById(cartItemCount.getId()).block();
        Assertions.assertNotNull(cic);
    }

    @Test
    public void deleteCartItemCount() {
        cartItemCount = cartItemCountRepository.save(cartItemCount).block();
        long id = cartItemCount.getId();
        cartItemCountRepository.delete(cartItemCount).block();
        CartItemCount cartItemCount1 = cartItemCountRepository.findById(id).block();
        Assertions.assertNull(cartItemCount1);
    }

    @Test
    public void findCartItemByOrderGetId(){
        cartItemCount = cartItemCountRepository.save(cartItemCount).block();
        Flux<CartItemCount> findOrder = cartItemCountRepository.findByOrderId(order.getId());
        List<CartItemCount> list = findOrder.collectList().block();
        int size = Objects.requireNonNull(list).size();
        Assertions.assertEquals(1, size);
        CartItemCount cartItemCount1 = list.get(0);
        Assertions.assertEquals(order.getId(),cartItemCount1.getOrderId());
    }

    @Test
    public void findCartItemByOrderIdAndItemGetId(){
        cartItemCount = cartItemCountRepository.save(cartItemCount).block();
        Mono<CartItemCount> findOrder = cartItemCountRepository.findByItemIdAndOrderId(item.getId(),order.getId());
        CartItemCount cartItemCount1 = findOrder.block();
        Assertions.assertEquals(order.getId(),cartItemCount1.getOrderId());
        Assertions.assertEquals(item.getId(),cartItemCount1.getItemId());
    }

}
