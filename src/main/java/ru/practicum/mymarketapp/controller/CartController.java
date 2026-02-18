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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
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
        List<CartItemCount> cartItemCounts = cartItemCountService.findCartItemCountByOrderId(order);
        List<ItemDto> items = cartItemCounts.stream()
                .peek(c-> c.getItemId().setCount(c.getQuantity()))
                .map(CartItemCount::getItemId).map(ItemDtoConverter::toDto).toList();
        model.addAttribute("items", items);

        if (order==null){
            model.addAttribute("total", 0);
        }else {
            model.addAttribute("total", order.getTotal());
        }
        return "cart";
    }

    @PostMapping("/cart/items")
    @Transactional
    public String cartItemsAction(Model model, @RequestParam Long id, @RequestParam String action) {
        Order order = orderService.findNewOrder();
        CartItemCount cartItemCount = itemService.findByOrderAndItemId(order,id);//Ищем предметы в корзине пользователя

        if (Objects.nonNull(cartItemCount)) {
            cartItemCount.setQuantity( "PLUS".equals(action) ?  cartItemCount.getQuantity()+1 : cartItemCount.getQuantity()-1);
        }

        BigDecimal total = new BigDecimal(0);
        if ("PLUS".equals(action)) {
            total = order.getTotal().add(new BigDecimal(cartItemCount.getItemId().getPrice()));
        } else if ("MINUS".equals(action)) {
            total = order.getTotal().subtract(new BigDecimal(cartItemCount.getItemId().getPrice()));
        }
        order.setTotal(total);

        if(cartItemCount.getQuantity()==0 || "DELETE".equals(action)){
            cartItemCountService.delete(cartItemCount);
            orderService.delete(order);
            model.addAttribute("items", Collections.emptyList());
        } else {
            cartItemCountService.save(cartItemCount);
            List<ItemDto> items = cartItemCountService.findAll().stream()
                    .peek(c-> c.getItemId().setCount(c.getQuantity()))
                    .map(CartItemCount::getItemId).map(ItemDtoConverter::toDto).toList();
            model.addAttribute("items", items);
            model.addAttribute("total", order.getTotal());
        }

        return "cart";
    }

}
