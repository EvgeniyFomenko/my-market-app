package ru.practicum.mymarketapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.ItemRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final OrderService orderService;
    private final ItemRepository itemRepository;
    private final CartItemCountService cartItemCountService;
    public ItemService(ItemRepository itemRepository, CartItemCountService cartItemCountService, OrderService orderService) {
        this.itemRepository = itemRepository;
        this.cartItemCountService = cartItemCountService;
        this.orderService = orderService;
    }

    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    public Page <Item> findItemsByTitle(String search, Pageable pageable) {
        Order order = orderService.findNewOrder();
        Page<Item> page;
        if (Objects.isNull(search) || search.isBlank()) {
            page = itemRepository.findAll(pageable);
        } else {
            page = itemRepository.findItemByTitle(search,pageable);
        }

        List<Item> items = page.getContent().stream().peek(item -> {
            CartItemCount cartItemCount = cartItemCountService.findByItemIdAndOrderId(item, order);
            if (Objects.nonNull(cartItemCount)) {
                item.setCount(cartItemCount.getQuantity());
            }
        }).collect(Collectors.toList());
        return new PageImpl<>(items,page.getPageable(),page.getTotalElements());
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Item findById(long id) {
        return itemRepository.findById(id).orElse(null);
    }


}
