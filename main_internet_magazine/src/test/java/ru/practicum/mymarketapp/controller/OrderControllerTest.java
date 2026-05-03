package ru.practicum.mymarketapp.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest(OrderController.class)
public class OrderControllerTest {
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
        order.setTotal(new BigDecimal(200));
        order.setPaid(false);
        item = new Item();
        item.setId(1);
        item.setCount(2);
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        cartItemCount = new CartItemCount();
        cartItemCount.setId(3L);
        cartItemCount.setItemId(item.getId());
        cartItemCount.setOrderId(order.getId());
    }

    @Test
    public void getOrders(){
        order.setPaid(true);
        Mockito.when(orderService.findPaidOrdersIsPaidTrue()).thenReturn(Flux.just(order));
        Mockito.when(cartItemCountService.findItemByOrderId(order.getId())).thenReturn(Flux.just(item));

        webTestClient.get().uri("/orders")
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
    public void getOrederById(){
        Mockito.when(orderService.findOrderById(order.getId())).thenReturn(Mono.just(order));
        Mockito.when(cartItemCountService.findItemByOrderId(order.getId())).thenReturn(Flux.just(item));

        webTestClient.get().uri("/orders/{id}", order.getId())
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
    public void getBuy() {
        Mockito.when(orderService.findNewOrderOrTakeNew()).thenReturn(Mono.just(order));
        Mockito.when(itemService.cachePageClear()).thenReturn(Mono.empty());
        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }



}
