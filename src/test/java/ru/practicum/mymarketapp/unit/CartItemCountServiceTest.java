package ru.practicum.mymarketapp.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.service.CartItemCountService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;

@SpringBootTest
public class CartItemCountServiceTest {
    @Autowired
    private CartItemCountService cartItemCountService;
    @MockitoBean
    private CartItemCountRepository cartItemCountRepository;
    CartItemCount cartItemCount;
    Order order;
    Item item;
    @BeforeEach
    void setUp() {
        reset(cartItemCountRepository);
        order = new Order();
        order.setTotal(new BigDecimal(100));
        order.setPaid(false);
        item = new Item();
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Title");
        item.setDescription("Description");
        cartItemCount = new CartItemCount();
        cartItemCount.setId(1L);
        cartItemCount.setItemId(item);
        cartItemCount.setOrderId(order);
    }

    @Test
    void findAll() {
        Mockito.doReturn(List.of(cartItemCount)).when(cartItemCountRepository).findAll();
        List<CartItemCount> cartItemCountFind = cartItemCountService.findAll();
        assertEquals(1, cartItemCountFind.size());
    }

    @Test
    void save() {
        Mockito.doReturn(cartItemCount).when(cartItemCountRepository).save(cartItemCount);
        cartItemCountService.save(cartItemCount);
    }

    @Test
    void findByItemIdAndOrderId() {
        Mockito.doReturn(cartItemCount).when(cartItemCountRepository).findByItemIdAndOrderId(item,order);
        cartItemCountService.findByItemIdAndOrderId(item, order);
    }

    @Test
    void deleteTest(){
        Mockito.doNothing().when(cartItemCountRepository).delete(cartItemCount);
        cartItemCountService.delete(cartItemCount);
    }

    @Test
    void findItemByOrderIdTest() {
        Mockito.doReturn(List.of(cartItemCount)).when(cartItemCountRepository).findByOrderId(order);
        cartItemCountService.findItemByOrderId(order);
    }

    @Test
    void findCartItemCountByOrderIdTest() {
        Mockito.doReturn(List.of(cartItemCount)).when(cartItemCountRepository).findByOrderId(order);
        cartItemCountService.findCartItemCountByOrderId(order);
    }
    @Test
    public void findByOrderAndItemId() {
        Mockito.doReturn(List.of(cartItemCount)).when(cartItemCountRepository).findByOrderId(order);
        cartItemCountService.findByOrderAndItemId(order, item.getId());
    }

}
