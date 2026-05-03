package ru.practicum.mymarketapp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.practicum.mymarketapp.PostgresqlTestContainer;
import ru.practicum.mymarketapp.RedisTestContainer;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.pojo.Action;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;


import java.math.BigDecimal;
import java.sql.SQLException;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ImportTestcontainers( {PostgresqlTestContainer.class, RedisTestContainer.class })
public class CartControllerTest {
    @Autowired
    private CartItemCountRepository cartItemCountRepository;
    @Autowired
    private WebTestClient webTestClient;
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
        item = itemRepository.save(item).block();
        order = orderRepository.save(order).block();
        cartItemCount = new CartItemCount();
        cartItemCount.setItemId(item.getId());
        cartItemCount.setOrderId(order.getId());
        cartItemCount.setQuantity(1);
        cartItemCountRepository.save(cartItemCount).subscribe();
    }

    @AfterEach
    public void after() {
        itemRepository.deleteById(item.getId()).subscribe();
        cartItemCountRepository.deleteById(cartItemCount.getId()).subscribe();
        orderRepository.deleteById(order.getId()).subscribe();
    }

    @Test
    public void testGetCartItems() throws Exception {
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                });
    }

    @Test
    public void cartItemsAction() throws Exception {
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<span>1</span>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                });

        webTestClient.post().uri(uriBuidler->uriBuidler.path("/cart/items")
                        .build())
                .body(BodyInserters.fromFormData("action", Action.PLUS.getFullName())
                        .with("id", item.getId().toString()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<span>2</span>"));
                });

        webTestClient.post().uri(uriBuidler->uriBuidler.path("/cart/items")
                        .build())
                .body(BodyInserters.fromFormData("action", Action.MINUS.getFullName())
                        .with("id", item.getId().toString()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<span>1</span>"));
                });

        webTestClient.post().uri(uriBuidler->uriBuidler.path("/cart/items")
                        .build())
                .body(BodyInserters.fromFormData("action", "MINUS")
                        .with("id", item.getId().toString()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<div class=\"row p-2\">"));
                });

    }
}
