package ru.practicum.mymarketapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.dto.ItemDto;
import ru.practicum.mymarketapp.entity.dto.ItemDtoConverter;
import ru.practicum.mymarketapp.pojo.*;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;
import ru.practicum.mymarketapp.service.OrderService;

import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
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
    public Mono<String> getItems(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(defaultValue = "NO") String sort,
                                 @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize, Model model, Authentication authentication) {
        Hooks.onOperatorDebug();
        Pageable pageable = PageableUtil.getPageable(pageNumber, pageSize, sort);
        String userLogin = "";
        boolean hasLogin = false;
        if(authentication != null) {
           userLogin = authentication.getName();
           hasLogin = true;
        }
        model.addAttribute("isLogin", hasLogin);
        model.addAttribute("authName", userLogin);
        Mono<PageCaching> pageableCaching = itemService.findItemsByTitle(search, pageable);

        if (Objects.isNull(authentication)) {
            return pageableCaching.map( pageCaching -> {
                List<ItemDto> itemsDto = pageCaching.getContent().stream().map(ItemDtoConverter::toDto).toList();
                Paging paging = new Paging(pageCaching.hasNext(), pageCaching.hasPrevious(), pageNumber, pageSize);
                model.addAttribute("paging", paging);
                model.addAttribute("items", itemsDto);
                model.addAttribute("sort", sort);
                log.error("out");
                return "items";
            });
        }
        Mono<Order> orderMono = orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin);
        return
                orderMono.zipWith(pageableCaching)
                .flatMap(tuple2-> {
                    List<Item> items = tuple2.getT2().getContent();

                    List<ItemDto> itemsDto = items.stream().map(ItemDtoConverter::toDto).toList();
                    Order order = tuple2.getT1();
                    return Flux.fromIterable(itemsDto).flatMap(item -> {
                        return cartItemCountService.findByItemIdAndOrderId(item.getId(), order.getId()).switchIfEmpty(getEmptyCartItemCount()).zipWith(Mono.just(item));
                    }).map(item -> {setItemQuantity(item.getT1(), item.getT2());
                    return item.getT2();}).collectList().switchIfEmpty(Mono.just(itemsDto)).zipWith(Mono.just(tuple2.getT2()));
                })
                .map(page -> {
                    List<ItemDto> items = page.getT1();

                    Paging paging = new Paging(page.getT2().hasNext(), page.getT2().hasPrevious(), pageNumber, pageSize);
                    model.addAttribute("paging", paging);
                    model.addAttribute("items", items);
                    model.addAttribute("sort", sort);

                    return "items";
                }
        );
    }

    private Mono<CartItemCount> getEmptyCartItemCount() {
        CartItemCount cartItemCount = new CartItemCount();
        cartItemCount.setQuantity(0);
        return Mono.just(cartItemCount);
    }

    private void setItemQuantity(CartItemCount cartItemCount, ItemDto item) {
        item.setCount(cartItemCount.getQuantity());
    }


    @Transactional
    @PostMapping("/items")
    public Mono<String> postItemsCart(@ModelAttribute FormData formData, Authentication authentication) {
        String userLogin = authentication.getName();
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
        return itemService.cacheItemClear().then(orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin))
        .flatMap(order -> cartItemCountService.createOrFindByOrderAndItemId(order.getId(), id)
                        ).flatMap(cartItemCount ->
                                cartItemCountService.changePriceCartByAction(cartItemCount, action)
                        ).flatMap(cartItemCount ->
                            orderService.changePriceOrderByActionOnCartItemCount(action, cartItemCount)
                        ).flatMap(cartItemCount -> {
                           String redirect = "redirect:/items?search=" + search + "&sort=" + finalSort + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize;
                           return itemService.cachePageClear().thenReturn(redirect);
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
    public Mono<String> postItemAction(@PathVariable Long id, @ModelAttribute FormData formData, Model model, Authentication authentication) {
        String action = formData.getAction();
        String userLogin = authentication.getName();
       return   itemService.cacheEvict(id).then(orderService.findNewOrderOrTakeNewByUserLoginOrNew(userLogin))
                .flatMap(p -> cartItemCountService.createOrFindByOrderAndItemId(p.getId(), id)
                ).flatMap(cartItemCount ->
                        cartItemCountService.changePriceCartByAction(cartItemCount, action)
                ).map(cartItemCount ->
                    orderService.changePriceOrderByActionOnCartItemCount(action, cartItemCount).thenReturn(cartItemCount)
                ).map(cartItemCount -> cartItemCount.filter(e-> e.getQuantity()==0)
                            .flatMap(e-> cartItemCountService.delete(e)
                                    .then(orderService.deleteById(e.getOrderId()))
                                    .thenReturn(e))
               ) .flatMap(e -> itemService.findById(id)).flatMap(item -> {
                            model.addAttribute("item", ItemDtoConverter.toDto(item));
                            return Mono.just("item");
                        }
                ).switchIfEmpty(Mono.error(new RuntimeException("item not found")));
    }

    @PostMapping("/item/add")
    public Mono<String> addItem(@ModelAttribute ItemDto itemDto, Model model) {
        return itemService.saveItem(ItemDtoConverter.fromDto(itemDto)).map(item -> {
            model.addAttribute("item", ItemDtoConverter.toDto(item));
            return "redirect:/items/" + item.getId();
        });
    }

    @GetMapping("/item/add")
    public Mono<String> getAddItem(Model model) {
        model.addAttribute("item", new ItemDto());
        return Mono.just("addItem");
    }
}
