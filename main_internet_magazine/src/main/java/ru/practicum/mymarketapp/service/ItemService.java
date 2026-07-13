package ru.practicum.mymarketapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.pojo.PageCaching;
import ru.practicum.mymarketapp.pojo.PageableCaching;
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
    @Cacheable(
            value = "Page",
            key = "#pageable")
    public Mono<PageCaching> findItemsByTitle(String search, Pageable pageable) {
        Flux<Item> page;
        if (Objects.isNull(search) || search.isBlank()) {
            page = itemRepository.findAllBy(pageable);
        } else {
            page = itemRepository.findItemByTitle(search,pageable);
        }
              return page.collectList().zipWith(itemRepository.count())
                .map(p ->{
                    PageableCaching pageableCaching = new PageableCaching(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().get().findFirst().orElse(new Sort.Order(Sort.Direction.ASC,"unsorted")).getProperty());
                    PageCaching pageCaching = new PageCaching(p.getT1(), pageableCaching, p.getT2());
                    return pageCaching;
                } );

    }
    @CacheEvict(
            value = "Item", // Имя кеша
            allEntries = true  // Удаление всех записей
    )
    public Mono<Void> cacheItemClear(){
        return Mono.empty();
    }

    @CacheEvict(
            value = "Page", // Имя кеша
            allEntries = true  // Удаление всех записей
    )
    public Mono<Void> cachePageClear(){
        return Mono.empty();
    }
    @CacheEvict(
            value = "Item",
            key = "#itemId"
    )
    public Mono<Void>  cacheEvict(Long itemId){
        return Mono.empty();
    }

    @CacheEvict(
            value = "Item", // Имя кеша
            key = "#item.id"   // Удаление всех записей
    )
    public Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }
    @CachePut(value = "Item", key = "#item.id")
    public Mono<Item> updateItem(Item item) {
        return itemRepository.save(item);
    }
    @Cacheable(
            value = "Item",
            key = "#id")
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
