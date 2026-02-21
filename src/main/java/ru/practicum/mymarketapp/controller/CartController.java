package ru.practicum.mymarketapp.controller;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.dto.ItemDto;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.util.List;
import java.util.Objects;


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
    public String cartItems(Model model) {
        Order order = orderService.findNewOrder();

        List<ItemDto> items = cartItemCountService.findItemByOrderId(order).stream()
                .map(ItemDtoConverter::toDto).toList();
        model.addAttribute("items", items);
        model.addAttribute("total", Objects.isNull(order)? 0 : order.getTotal());

        return "cart";
    }

    @PostMapping("/cart/items")
    @Transactional
    public String cartItemsAction(Model model, @RequestParam Long id, @RequestParam String action) {
        Order order = orderService.findNewOrder();
        Item item = itemService.findById(id);
        CartItemCount cartItemCount = cartItemCountService.createOrFindByOrderAndItemId(order,item);
        cartItemCountService.changePriceCartByAction(cartItemCount,action);
        orderService.changePriceOrderByActionOnCartItemCount(order,action,cartItemCount);

        List<ItemDto> items = cartItemCountService.findItemByOrderId(order).stream()
                .map(ItemDtoConverter::toDto).toList();
        model.addAttribute("items", items);
        model.addAttribute("total", Objects.isNull(order)? 0 : order.getTotal());

        return "cart";
    }

}
