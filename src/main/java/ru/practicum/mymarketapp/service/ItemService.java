package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private ItemRepository itemRepository;
    private CartItemCountService cartItemCountService;
    public ItemService(ItemRepository itemRepository, CartItemCountService cartItemCountService) {
        this.itemRepository = itemRepository;
        this.cartItemCountService = cartItemCountService;
    }

    public List<Item> getItems() {
        return itemRepository.findAll();
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

    public CartItemCount findByOrderAndItemId(Order order, Long id) {
        List<CartItemCount> cartItemCountUser = cartItemCountService.findCartItemCountByOrderId(order);
        Optional<CartItemCount> cartItemCountOptional = cartItemCountUser.stream()
                .filter(cartItemCount -> cartItemCount.getItemId().getId() == id).findAny();
        return cartItemCountOptional.orElse(null);
    }
}
