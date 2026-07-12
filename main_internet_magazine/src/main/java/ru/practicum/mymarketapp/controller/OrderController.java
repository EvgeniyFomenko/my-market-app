package ru.practicum.mymarketapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.extras.springsecurity6.auth.Authorization;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.entity.dto.OrderDtoConverter;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.util.stream.Collectors;

@Controller
public class OrderController {

    private final CartItemCountService cartItemCountService;
    private final OrderService orderService;
    private final ItemService itemService;

    public OrderController(CartItemCountService cartItemCountService, OrderService orderService, ItemService itemService) {
        this.cartItemCountService = cartItemCountService;
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @GetMapping("/orders")
    public Mono<String> getOrders(Model model, Authentication authentication) {
        String userLogin = authentication.getName();
        return orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin)
                .flatMap(e->
                         cartItemCountService.findItemByOrderId(e.getId()).collectList().zipWith(Mono.just(e))
                                .map(p -> OrderDtoConverter.toDto(p.getT2(), p.getT1().stream().map(ItemDtoConverter::toDto)
                                        .collect(Collectors.toList())))
                    ).flatMap(
                        e-> {
                            model.addAttribute("orders", e);
                            return Mono.just("orders");
                        });
    }

    @GetMapping ("/orders/{id}")
    public Mono<String> getOrder(@PathVariable Long id, Model model) {
       return orderService.findOrderById(id).flatMap(order ->
            cartItemCountService.findItemByOrderId(order.getId()).collectList().zipWith(Mono.just(order))
                    .map(p->
                        OrderDtoConverter.toDto(p.getT2(), p.getT1().stream().map(ItemDtoConverter::toDto).collect(Collectors.toList()))
                    ).flatMap(orderDto-> {
                        model.addAttribute("order", orderDto);
                        return Mono.just("order");
                    })
        );
    }

    @PostMapping("/buy")
    @Transactional
    public Mono<String> setBuy(Model model, Authentication authentication) {
        String userLogin = authentication.getName();
       return itemService.cachePageClear().then(orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin))
               .flatMap(order ->
                  cartItemCountService.findItemByOrderId(order.getId()).collectList().doOnNext(e-> {
                       if (e.isEmpty()) {
                           throw new RuntimeException("Карзина пустая");
                       }
                   }).then(orderService.updatePaid(order)).thenReturn(order)
               ).map(order-> {
           model.addAttribute("newOrder", true);
           return "redirect:orders/"+order.getId();
       });
    }
}
