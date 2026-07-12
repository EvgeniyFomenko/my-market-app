package ru.practicum.mymarketapp.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.extras.springsecurity6.auth.Authorization;
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
    public Mono<String> cartItems(Model model, Authentication authentication) {
        String userLogin = authentication.getName();
        return orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin).doOnNext(order -> model.addAttribute("total", order.getTotal()))
                .map(order -> cartItemCountService.findItemByOrderId(order.getId()))
                .flatMap(Flux::collectList)
                .map(e -> e.stream().map(ItemDtoConverter::toDto)
                        .collect(Collectors.toList())).zipWith(orderService.getBalance()).map(e ->
                        {
                            model.addAttribute("balance", e.getT2().getBalance());
                            model.addAttribute("items", e.getT1());
                            return "cart";
                        }
                ).switchIfEmpty(getEmptyCart(model));
    }

    private Mono<String> getEmptyCart(Model model) {
        return orderService.getBalance().map(balance -> {
            model.addAllAttributes(Map.of("items", new ArrayList<ItemDto>(), "total", 0, "balance", balance.getBalance()));
            return "cart";

        });
    }

    @PostMapping("/cart/items")
    @Transactional
    public Mono<String> cartItemsAction(Model model, @ModelAttribute FormData formData, Authentication authentication) {
        Long id = Long.parseLong(formData.getId());
        String action = formData.getAction();
        String userLogin = authentication.getName();
        return itemService.cacheItemClear().then(orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin))
                .flatMap(order -> cartItemCountService.createOrFindByOrderAndItemId(order.getId(), id))
                .flatMap(cartItemCount1 -> cartItemCountService.changePriceCartByAction(cartItemCount1, action))
                .flatMap(e -> orderService.changePriceOrderByActionOnCartItemCount(action, e))
                .flatMap(e -> {
                    model.addAttribute("total", e.getTotal());
                    return cartItemCountService.findItemByOrderId(e.getId()).map(ItemDtoConverter::toDto).collectList();
                }).zipWith(orderService.getBalance()).map(e ->
                {
                    model.addAttribute("balance", e.getT2().getBalance());
                    model.addAttribute("items", e.getT1());
                    return "cart";
                }).switchIfEmpty(getEmptyCart(model));


    }
}
