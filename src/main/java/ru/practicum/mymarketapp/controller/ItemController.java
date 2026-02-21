package ru.practicum.mymarketapp.controller;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.List;

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
                           @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize, Model model) {
        Pageable pageable = PagableUtil.getPageable(pageNumber, pageSize, sort);
        Page<Item> page = itemService.findItemsByTitle(search,pageable);
        List<ItemDto> items = page.getContent().stream().map(ItemDtoConverter::toDto).toList();
        Paging paging = new Paging(page.hasNext(),page.hasPrevious(), pageNumber, pageSize);
        model.addAttribute("items", items);
        model.addAttribute("paging", paging);
        model.addAttribute("sort",sort);
        return "items";
    }

    @PostMapping("/items")
    @Transactional
    public String postItemsCart(@RequestParam Long id, @RequestParam(required = false) String search,
                                @RequestParam(defaultValue = "NO") String sort,
                                @RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "5") Integer pageSize,
                                @RequestParam String action) {

        Order order = orderService.findNewOrder();
        Item item = itemService.findById(id);
        CartItemCount cartItemCount = cartItemCountService.createOrFindByOrderAndItemId(order,item);
        cartItemCountService.changePriceCartByAction(cartItemCount,action);
        orderService.changePriceOrderByActionOnCartItemCount(order,action,cartItemCount);

        if (cartItemCount.getQuantity() == 0) {
            cartItemCountService.delete(cartItemCount);
            orderService.delete(order);
        }

        return "redirect:/items?search=" + search + "&sort=" + sort + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize;
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable int id, Model model) {
        model.addAttribute("item", ItemDtoConverter.toDto(itemService.findById(id)));
        return "item";
    }
}
