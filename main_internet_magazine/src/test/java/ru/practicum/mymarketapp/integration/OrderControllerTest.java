package ru.practicum.mymarketapp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.practicum.mymarketapp.PostgresqlTestContainer;
import ru.practicum.mymarketapp.RedisTestContainer;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ImportTestcontainers( {PostgresqlTestContainer.class, RedisTestContainer.class })
public class OrderControllerTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemCountRepository cartItemCountRepository;
    @Autowired
    private WebTestClient mockMvc;
    private Item item;
    private Order order;
    private CartItemCount cartItemCount;


    @BeforeEach
    public void setUp() throws SQLException {
        order = new Order();
        order.setTotal(new BigDecimal(200));
        order.setPaid(false);
        item = new Item();
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        item = itemRepository.save(item).block();
        order = orderRepository.save(order).block();
        cartItemCount = new CartItemCount();
        cartItemCount.setItemId(item.getId());
        cartItemCount.setOrderId(order.getId());
        cartItemCount.setQuantity(2);
        cartItemCount = cartItemCountRepository.save(cartItemCount).block();
    }

    @AfterEach
    public void after(){
        itemRepository.deleteById(item.getId()).subscribe();
        orderRepository.deleteById(order.getId()).subscribe();
        cartItemCountRepository.deleteById(cartItemCount.getId()).subscribe();
    }

    @Test
    public void getOrders() {
        order.setPaid(true);
        order = orderRepository.save(order).block();
        mockMvc.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<li class=\"list-group-item\">Auto (2 шт.) 200 руб.</li>"));
                    assertTrue(body.contains("<b>Сумма: 200 руб.</b>"));
                });
    }

    @Test
    public void getOrder() {
        mockMvc.get().uri("/orders/{id}", order.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h2>Заказ №"+order.getId()+"</h2>"));
                    assertTrue(body.contains("<b>Auto</b>"));
                });
    }

    @Test
    public void setBuy() {
        order.setPaid(false);
        orderRepository.save(order);
        mockMvc.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

        mockMvc.get().uri("/orders/{id}", order.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h2>Заказ №"+order.getId()+"</h2>"));
                    assertTrue(body.contains("<b>Auto</b>"));
                });
    }
}