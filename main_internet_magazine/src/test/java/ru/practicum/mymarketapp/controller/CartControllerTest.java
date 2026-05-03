package ru.practicum.mymarketapp.controller;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.pojo.Action;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest(CartController.class)
public class CartControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    CartItemCountService cartItemCountService;
    @MockitoBean
    OrderService orderService;
    @MockitoBean
    ItemService itemService;
    @MockitoBean
    CacheManager cacheManager;

    static CartItemCount cartItemCount ;
    static Order order ;
    static Item item;

    @BeforeAll
    static void setup() {
        order = new Order();
        order.setId(2L);
        order.setTotal(new BigDecimal(100));
        order.setPaid(false);
        item = new Item();
        item.setId(1);
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        cartItemCount = new CartItemCount();
        cartItemCount.setId(3L);
        cartItemCount.setItemId(item.getId());
        cartItemCount.setOrderId(order.getId());

    }

    @Test
    void testGetCartItems() {
        Mockito.when(orderService.findNewOrderOrTakeNew()).thenReturn(Mono.just(order));
        Mockito.when(cartItemCountService.findItemByOrderId(order.getId())).thenReturn(Flux.just(item));
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                });
    }

    @Test
    void testGetCartItemCounts() {;
        Mockito.when(orderService.findNewOrderOrTakeNew()).thenReturn(Mono.just(order));
        Mockito.when(cartItemCountService.findItemByOrderId(order.getId())).thenReturn(Flux.just(item));
        Mockito.when(cartItemCountService.createOrFindByOrderAndItemId(order.getId(),item.getId())).thenReturn(Mono.just(cartItemCount));
        CartItemCount cartItemCount1 = new CartItemCount();
        cartItemCount1.setId(3L);
        cartItemCount1.setItemId(item.getId());
        cartItemCount1.setOrderId(order.getId());
        cartItemCount1.setQuantity(1);
        Mockito.when(cartItemCountService.changePriceCartByAction(cartItemCount, Action.PLUS.getFullName())).thenReturn(Mono.just(cartItemCount1));
        Mockito.when(orderService.changePriceOrderByActionOnCartItemCount(Action.PLUS.getFullName(),cartItemCount1)).thenReturn(Mono.just(order));
        Mockito.when(itemService.findById(item.getId())).thenReturn(Mono.just(item));
        Mockito.when(itemService.cacheItemClear()).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuidler->uriBuidler.path("/cart/items")
                        .build())
                .body(BodyInserters.fromFormData("action", Action.PLUS.getFullName())
                        .with("id", item.getId().toString()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                    assertTrue(body.contains("<span>1</span>"));
                });
    }
}

