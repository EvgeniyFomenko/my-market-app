package ru.practicum.mymarketapp.controller;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.dto.ItemDto;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.pojo.Paging;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class ItemController {
    private final ItemService itemService;
    private final CartItemCountService cartItemCountService;
    private final OrderService orderService;

    public ItemController(ItemService itemService, CartItemCountService cartItemCountService, OrderService orderService) {
        this.itemService = itemService;
        this.cartItemCountService = cartItemCountService;
        this.orderService = orderService;
    }

    @GetMapping({"/","/items"})
    public String getItems(@RequestParam(required = false) String search, @RequestParam(defaultValue = "NO") String sort,
                           @RequestParam(defaultValue = "2") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize, Model model) {

        List<List<ItemDto>> itemsByPage = new ArrayList<>();
        Order order = orderService.findNewOrder();

        List<ItemDto> items = itemService.getItems().stream().peek(item -> {
            CartItemCount cartItemCount = cartItemCountService.findByItemIdAndOrderId(item, order);
            if (Objects.nonNull(cartItemCount)) {
                item.setCount(cartItemCount.getQuantity());
            }
        }).map(ItemDtoConverter::toDto).toList();

        int countPages;
        if (items.size() > 0 && items.size() <= pageSize) {
            countPages = 1;
        } else {
            countPages = items.size() / pageSize;
        }
        for (int i = 0; i < countPages; i++) {
            if (items.size() <= pageSize) {
                itemsByPage.add(items);
            } else {
                List<ItemDto> partition = items.subList(i * pageSize, i * pageSize + pageSize);
                itemsByPage.add(partition);
            }
        }
        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);
        paging.setHasNext(false);
        paging.setHasPrevious(false);
        model.addAttribute("items", itemsByPage);
        model.addAttribute("paging", paging);
        return "items";
    }

    @PostMapping("/items")
    @Transactional
    public String postItemsCart(@RequestParam Long id, @RequestParam(required = false) String search,
                                @RequestParam(defaultValue = "NO") String sort,
                                @RequestParam(defaultValue = "2") Integer pageNumber,
                                @RequestParam(defaultValue = "5") Integer pageSize,
                                @RequestParam String action) {

        Order order = orderService.findNewOrder();
        CartItemCount cartItemCount = itemService.findByOrderAndItemId(order,id);//Ищем предметы в корзине пользователя

        if (Objects.isNull(order)) { // Если карзина пустая то создаем новую карзину
            order = new Order();
            order.setPaid(false);
            order.setTotal(new BigDecimal(0));
            order = orderService.addOrder(order);
        }

        if (Objects.nonNull(cartItemCount)) { //добаляем или удаляем количество товаров в корзине
            cartItemCount.setQuantity("PLUS".equals(action) ? cartItemCount.getQuantity() + 1 : cartItemCount.getQuantity() - 1);
        } else {
            cartItemCount = new CartItemCount();
            Item item = itemService.findById(id);
            cartItemCount.setItemId(item);
            cartItemCount.setOrderId(order);
            if ("PLUS".equals(action)) {
                cartItemCount.setQuantity(1);
            }
        }

        BigDecimal total;
        if ("PLUS".equals(action)) {
            total = order.getTotal().add(new BigDecimal(cartItemCount.getItemId().getPrice()));
        } else {
            total = order.getTotal().subtract(new BigDecimal(cartItemCount.getItemId().getPrice()));
        }
        order.setTotal(total);

        if (cartItemCount.getQuantity() == 0) {
            cartItemCountService.delete(cartItemCount);
            orderService.delete(order);
        } else {
            cartItemCountService.save(cartItemCount);
        }

        return "redirect:/items?search=" + search + "&sort=" + sort + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize;
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable int id, Model model) {
        model.addAttribute("item", ItemDtoConverter.toDto(itemService.findById(id)));
        return "item";
    }
}
