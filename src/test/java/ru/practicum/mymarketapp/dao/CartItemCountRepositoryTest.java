package ru.practicum.mymarketapp.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
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
        itemRepository.save(item);
        order = orderRepository.save(order);
        cartItemCount = new CartItemCount();
        cartItemCount.setItemId(item);
        cartItemCount.setOrderId(order);
    }
    @Test
    public void saveCartItemCount() {
        cartItemCountRepository.save(cartItemCount);
        CartItemCount cic =  cartItemCountRepository.findById(cartItemCount.getId()).orElse(null);
        Assertions.assertNotNull(cic);
    }

    @Test
    public void deleteCartItemCount() {
        cartItemCountRepository.save(cartItemCount);
        long id = cartItemCount.getId();
        cartItemCountRepository.delete(cartItemCount);
        CartItemCount cartItemCount1 = cartItemCountRepository.findById(id).orElse(null);
        Assertions.assertNull(cartItemCount1);
    }

    @Test
    public void findCartItemByOrderGetId(){
        cartItemCountRepository.save(cartItemCount);
        List<CartItemCount> findOrder = cartItemCountRepository.findByOrderId(order);
        int size = findOrder.size();
        Assertions.assertEquals(1, size);
        CartItemCount cartItemCount1 = findOrder.get(0);
        Assertions.assertEquals(order.getId(),cartItemCount1.getOrderId().getId());
    }

    @Test
    public void findCartItemByOrderIdAndItemGetId(){
        cartItemCountRepository.save(cartItemCount);
        CartItemCount findOrder = cartItemCountRepository.findByItemIdAndOrderId(item,order);
        Assertions.assertEquals(order.getId(),findOrder.getOrderId().getId());
        Assertions.assertEquals(item.getId(),findOrder.getItemId().getId());
    }

}
