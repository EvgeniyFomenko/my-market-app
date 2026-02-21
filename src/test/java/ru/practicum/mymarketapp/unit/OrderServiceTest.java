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
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.reset;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @MockitoBean
    private OrderRepository orderRepository;
    @MockitoBean
    private CartItemCountRepository cartItemCountRepository;

    CartItemCount cartItemCount;
    Order order;
    Item item;

    @BeforeEach
    void setUp() {
        reset(orderRepository);
        order = new Order();
        order.setId(1L);
        order.setTotal(new BigDecimal(100));
        order.setPaid(false);
        item = new Item();
        item.setId(2L);
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Title");
        item.setDescription("Description");
        cartItemCount = new CartItemCount();
        cartItemCount.setId(3L);
        cartItemCount.setItemId(item);
        cartItemCount.setOrderId(order);
    }

    @Test
    public void findNewOrder() {
        Mockito.doReturn(List.of(order)).when(orderRepository).findByIsPaidFalse();
        orderService.findNewOrder();
    }

    @Test
    public void addOrder() {
        Mockito.doReturn(order).when(orderRepository).save(order);
        orderService.addOrder(order);
    }

    @Test
    public void deleteOrder() {
        Mockito.doNothing().when(orderRepository).delete(order);
        orderService.delete(order);
    }

    @Test
    public void findPaidOrdersIsPaidTrue() {
        Mockito.doReturn(List.of(order)).when(orderRepository).findByIsPaidTrue();
        orderService.findPaidOrdersIsPaidTrue();
    }

    @Test
    public void updatePaid() {
        Mockito.doReturn(order).when(orderRepository).save(order);
        orderService.updatePaid(order);
    }

    @Test
    public void findOrderById(){
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());
        orderService.findOrderById(order.getId());
    }


}
