package ru.practicum.mymarketapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.repository.ItemRepository;

import java.util.Objects;

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

    public Flux<Item> getItems() {
        return itemRepository.findAll();
    }

    public Mono<Page<Item>> findItemsByTitle(String search, Pageable pageable) {
        Flux<Item> page;
        if (Objects.isNull(search) || search.isBlank()) {
            page = itemRepository.findAllBy(pageable);
        } else {
            page = itemRepository.findItemByTitle(search,pageable);
        }

        return page.flatMap(item ->
                orderService.findNewOrder().flatMap(e-> cartItemCountService.findByItemIdAndOrderId(item.getId(),e.getId()))
                        .map(e-> {
                            if(e.getItemId().equals(item.getId())) {
                                item.setCount(e.getQuantity());
                            }
                            return item;
                        }).switchIfEmpty(Mono.just(item))
        ).collectList()
                .zipWith(this.itemRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }


    public Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Item> updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Item> findById(Long id) {
        Mono<Item> itemMono = itemRepository.findById(id);
        return   orderService.findNewOrder().flatMap(e-> cartItemCountService.findByItemIdAndOrderId(id,e.getId())).zipWith(itemMono)
                .map(e-> {
                    if(e.getT1().getItemId().equals(id)) {
                        e.getT2().setCount(e.getT1().getQuantity());
                    }
                    return e.getT2();
                }).switchIfEmpty(itemMono);
    }


}
