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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {
    @Autowired
    private CartItemCountRepository cartItemCountRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private Order order;
    private CartItemCount cartItemCount;

    @BeforeEach
    public void setUp() throws SQLException {
        order = new Order();
        order.setTotal(new BigDecimal(100));
        order.setPaid(false);
        item = new Item();
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        itemRepository.save(item);
        order = orderRepository.save(order);
        cartItemCount = new CartItemCount();
        cartItemCount.setItemId(item);
        cartItemCount.setOrderId(order);
        cartItemCountRepository.save(cartItemCount);
    }

    @AfterEach
    public void after(){
        itemRepository.deleteById(item.getId());
        cartItemCountRepository.deleteById(cartItemCount.getId());
        orderRepository.deleteById(order.getId());
    }

    @Test
    public void testGetCartItems() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<h5 class=\"card-title\">Auto</h5>")))
                .andExpect(content().string(containsString("<p class=\"card-text\">Description</p>")));
    }

    @Test
    public void  cartItemsAction() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<span>0</span>")));
        mockMvc.perform(post("/cart/items")
                        .param("id", item.getId().toString())
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<span>1</span>")));

        mockMvc.perform(post("/cart/items")
                        .param("id", item.getId().toString())
                        .param("action", "MINUS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<div class=\"row p-2\">\n" +
                        "            <div class=\"col\">")));
    }
}
