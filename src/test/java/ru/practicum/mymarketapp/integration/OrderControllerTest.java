package ru.practicum.mymarketapp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemCountRepository cartItemCountRepository;
    @Autowired
    private MockMvc mockMvc;
    private Item item;
    private Order order;
    private CartItemCount cartItemCount;


    @BeforeEach
    public void setUp() throws SQLException {
        order = new Order();
        item = new Item();
        cartItemCount = new CartItemCount();
        order.setTotal(new BigDecimal(200));
        order.setPaid(true);
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        cartItemCount.setItemId(item);
        cartItemCount.setOrderId(order);
        cartItemCount.setQuantity(2);
        orderRepository.save(order);
        itemRepository.save(item);
        cartItemCountRepository.save(cartItemCount);
    }

    @AfterEach
    public void after(){
        itemRepository.deleteById(item.getId());
        orderRepository.deleteById(order.getId());
        cartItemCountRepository.deleteById(cartItemCount.getId());
    }

    @Test
    public void getOrders() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<li class=\"list-group-item\">Auto (2 шт.) 200 руб.</li>")))
                .andExpect(content().string(containsString("<b>Сумма: 200 руб.</b>")));
    }

    @Test
    public void getOrder() throws Exception {
        mockMvc.perform(get("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h2>Заказ №"+order.getId()+"</h2>")))
                .andExpect(content().string(containsString("<b>Auto</b>")));
    }

    @Test
    public void setBuy() throws Exception {
        order.setPaid(false);
        orderRepository.save(order);
        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h2>Заказ №"+order.getId()+"</h2>")))
                .andExpect(content().string(containsString("<b>Auto</b>")));
    }
}