package ru.practicum.mymarketapp.controller;


import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.dto.ItemDto;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.pojo.FormData;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.util.*;
import java.util.stream.Collectors;


@Controller
public class CartController {
    private final CartItemCountService cartItemCountService;
    private final OrderService orderService;
    private final ItemService itemService;

    public CartController(CartItemCountService cartItemCountService, OrderService orderService, ItemService itemService) {
        this.cartItemCountService = cartItemCountService;
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @GetMapping("/cart/items")
    public Mono<String> cartItems(Model model) {
        return orderService.findNewOrderOrTakeNew().doOnNext(order -> model.addAttribute("total", order.getTotal()))
                .map(order -> cartItemCountService.findItemByOrderId(order.getId()))
                .flatMap(Flux::collectList)
                .map(e -> e.stream().map(ItemDtoConverter::toDto)
                        .collect(Collectors.toList())).map(e ->
                        {
                            model.addAttribute("items", e);
                            return "cart";
                        }
                ).switchIfEmpty(getEmptyCart(model));
    }

    private Mono<String> getEmptyCart(Model model) {
        model.addAllAttributes(Map.of("items", new ArrayList<ItemDto>(), "total", 0));
        return Mono.just("cart");
    }

    @PostMapping("/cart/items")
    @Transactional
    public Mono<String> cartItemsAction(Model model, @ModelAttribute FormData formData) {
        Long id = Long.parseLong(formData.getId());
        String action = formData.getAction();
        return orderService.findNewOrderOrTakeNew().log()
                .flatMap(order -> cartItemCountService.createOrFindByOrderAndItemId(order.getId(),id))
                .flatMap(cartItemCount1 -> cartItemCountService.changePriceCartByAction(cartItemCount1, action))
                .flatMap(e -> orderService.changePriceOrderByActionOnCartItemCount(action, e))
                .flatMap(e -> {
                    model.addAttribute("total", e.getTotal());
                    return cartItemCountService.findItemByOrderId(e.getId()).map(ItemDtoConverter::toDto).collectList();
                }).map(e -> {
                    model.addAttribute("items", e);
                    return "cart";
                }).switchIfEmpty(Mono.just("cart")
                        .doOnNext(e -> {
                            model.addAttribute("items", Collections.emptyList());
                            model.addAttribute("total", 0);
                        }));


    }
}
