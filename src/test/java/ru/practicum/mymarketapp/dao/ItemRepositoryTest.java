package ru.practicum.mymarketapp.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.repository.ItemRepository;

import java.util.List;


@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    Item item;

    @BeforeEach
    public void init() {
        item = new Item();
        item.setCount(0);
        item.setPrice(100);
        item.setTitle("Item");
        item.setDescription("Description");
    }

    @Test
    public void saveItem() {
        Item item = new Item();
        item.setCount(0);
        item.setPrice(100);
        item.setTitle("Item");
        item.setDescription("Description");
        System.out.println("Before safe "+ item.toString());
        Item saveItem = itemRepository.save(item);
        Assertions.assertEquals(item,saveItem);
        System.out.println(saveItem.toString());
        System.out.println("After safe" + item.toString());
    }

    @Test
    public void deleteItem() {
        itemRepository.save(item);
        itemRepository.deleteById(item.getId());
        List<Item> items = itemRepository.findAll();
        Assertions.assertEquals(0,items.size());
    }

    @Test
    public void findByGetId(){
        itemRepository.save(item);
        List<Item> items = itemRepository.findAll();
        System.out.println("Items count" + items.size());
        Item findItem = itemRepository.findById(item.getId()).orElse(null);
        Assertions.assertNotNull(findItem);
    }
}
