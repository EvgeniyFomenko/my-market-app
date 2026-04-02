package ru.practicum.mymarketapp.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
        cartItemCount.setItemId(item.getId());
        cartItemCount.setOrderId(order.getId());
    }

    @Test
    void findAll() {
        Mockito.doReturn(Flux.just(List.of(cartItemCount))).when(cartItemCountRepository).findAll();
        List<CartItemCount> cartItemCountFind = cartItemCountService.findAll().collectList().block();
        assertEquals(1, cartItemCountFind.size());
    }

    @Test
    void save() {
        Mockito.doReturn(Mono.just(cartItemCount)).when(cartItemCountRepository).save(cartItemCount);
        cartItemCountService.save(cartItemCount);
    }

    @Test
    void findByItemIdAndOrderId() {
        Mockito.doReturn(Mono.just(cartItemCount)).when(cartItemCountRepository).findByItemIdAndOrderId(item.getId(),order.getId());
        cartItemCountService.findByItemIdAndOrderId(item.getId(), order.getId());
    }

    @Test
    void deleteTest(){
        Mockito.doReturn(Mono.just(Void.class)).when(cartItemCountRepository).delete(cartItemCount);
        cartItemCountService.delete(cartItemCount);
    }

    @Test
    void findItemByOrderIdTest() {
        Mockito.doReturn(Flux.just(List.of(cartItemCount))).when(cartItemCountRepository).findByOrderId(order.getId());
        cartItemCountService.findItemByOrderId(order.getId());
    }

    @Test
    void findCartItemCountByOrderIdTest() {
        Mockito.doReturn(Flux.just(List.of(cartItemCount))).when(cartItemCountRepository).findByOrderId(order.getId());
        cartItemCountService.findCartItemCountByOrderId(order.getId());
    }
    @Test
    public void findByOrderAndItemId() {
        Mockito.doReturn(Flux.just(List.of(cartItemCount))).when(cartItemCountRepository).findByOrderId(order.getId());
        cartItemCountService.findByOrderAndItemId(order.getId(), item.getId());
    }

}
