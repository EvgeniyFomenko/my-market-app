package ru.practicum.mymarketapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.dto.ItemDto;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.entity.dto.OrderDto;
import ru.practicum.mymarketapp.entity.dto.OrderDtoConverter;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.OrderService;

import java.util.List;

@Controller
public class OrderController {

    private final CartItemCountService cartItemCountService;
    private final OrderService orderService;

    public OrderController(CartItemCountService cartItemCountService, OrderService orderService) {
        this.cartItemCountService = cartItemCountService;
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        List<OrderDto> orderDtos = orderService.findPaidOrdersIsPaidTrue().stream()
                .map(o-> OrderDtoConverter.toDto(o, cartItemCountService.findItemByOrderId(o).stream()
                                                            .map(ItemDtoConverter::toDto).toList())).toList();
        model.addAttribute("orders", orderDtos);
        return "orders";
    }

    @GetMapping ("/orders/{id}")
    public String getOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findOrderById(id);
        OrderDto orderDto = new OrderDto();
        List<ItemDto> itemDtos = cartItemCountService.findItemByOrderId(order).stream().map(ItemDtoConverter::toDto).toList();
        orderDto.setItems(itemDtos);
        orderDto.setId(order.getId());
        orderDto.setTotalSum(order.getTotal().longValue());
        model.addAttribute("order", orderDto);
        return "order";
    }

    @PostMapping("/buy")
    @Transactional
    public String setBuy(RedirectAttributes redirectAttrs) {
        Order order = orderService.findNewOrder();
        orderService.updatePaid(order);
        redirectAttrs.addAttribute("newOrder", true);
        String redirectUrl = "redirect:orders/"+order.getId();
        return redirectUrl;
    }
}
