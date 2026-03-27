package ru.practicum.mymarketapp.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.dto.ItemDto;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.pojo.FormData;
import ru.practicum.mymarketapp.pojo.Paging;
import ru.practicum.mymarketapp.pojo.VariableSort;
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

    @GetMapping({"/", "/items"})
    public Mono<String> getItems(@RequestParam(required = false) String search, @RequestParam(defaultValue = "NO") String sort,
                                 @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize, Model model) {
        Pageable pageable = PagableUtil.getPageable(pageNumber, pageSize, sort);
        return itemService.findItemsByTitle(search, pageable).map(
                page -> {
                    List<ItemDto> items = page.getContent().stream().map(ItemDtoConverter::toDto).toList();
                    Paging paging = new Paging(page.hasNext(), page.hasPrevious(), pageNumber, pageSize);
                    model.addAttribute("paging", paging);
                    model.addAttribute("items", items);
                    model.addAttribute("sort", sort);
                    return "items";
                }
        ).switchIfEmpty(Mono.just("items"));
    }

    @Transactional
    @PostMapping("/items")
    public Mono<String> postItemsCart(@ModelAttribute FormData formData) {

        String idStr = formData.getId();
        String action = formData.getAction();
        String search = formData.getSearch();
        String sort = formData.getSort();
        String pageNumberStr = formData.getPageNumber();
        String pageSizeStr = formData.getPageSize();
        Long id = (Long.parseLong(idStr));
        Integer pageNumber = (pageNumberStr != null ? Integer.parseInt(pageNumberStr) : 1);
        Integer pageSize = (pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 5);
        String finalSort;
        if (sort == null) {
            sort = VariableSort.NO.getFullName();
        }
        finalSort = sort;

        return
                orderService.findNewOrderOrTakeNew()
                .flatMap(order -> cartItemCountService.createOrFindByOrderAndItemId(order.getId(), id)
                ).log().flatMap(cartItemCount ->
                        cartItemCountService.changePriceCartByAction(cartItemCount, action)
                ).log().map(cartItemCount -> {
                    orderService.changePriceOrderByActionOnCartItemCount(action, cartItemCount).subscribe();
                    if (cartItemCount.getQuantity() == 0) {
                        cartItemCountService.delete(cartItemCount).subscribe();
                        Order order = new Order();
                        order.setId(cartItemCount.getId());
                        orderService.delete(order).subscribe();
                    }
                    return "redirect:/items?search=" + search + "&sort=" + finalSort + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize;
                });

    }

    @GetMapping("/items/{id}")
    public Mono<String> getItem(@PathVariable Long id, Model model) {
       return itemService.findById(id).flatMap(item -> {
            model.addAttribute("item", ItemDtoConverter.toDto(item));
            return Mono.just("item");
        }).switchIfEmpty(Mono.error(new RuntimeException("item not found")));
    }

    @PostMapping("/items/{id}")
    public Mono<String> postItemAction(@PathVariable Long id, @ModelAttribute FormData formData,Model model) {
        String action = formData.getAction();
        return orderService.findNewOrderOrTakeNew()
                .flatMap(p -> cartItemCountService.createOrFindByOrderAndItemId(p.getId(), id)
                ).log().flatMap(cartItemCount ->
                        cartItemCountService.changePriceCartByAction(cartItemCount, action)
                ).log().map(cartItemCount -> {
                    orderService.changePriceOrderByActionOnCartItemCount(action, cartItemCount).subscribe();
                    if (cartItemCount.getQuantity() == 0) {
                        cartItemCountService.delete(cartItemCount).subscribe();
                        Order order = new Order();
                        order.setId(cartItemCount.getId());
                        orderService.delete(order).subscribe();
                    }
                    return cartItemCount;
                }).flatMap(e-> itemService.findById(id)).flatMap(item -> { model.addAttribute("item", ItemDtoConverter.toDto(item));
                    return Mono.just("item");
                }
                ).switchIfEmpty(Mono.error(new RuntimeException("item not found")));
    }
}
