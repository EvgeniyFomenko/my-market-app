package ru.practicum.mymarketapp.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.mymarketapp.PostgresqlTestContainer;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.repository.ItemRepository;

import java.util.List;


@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresqlTestContainer.class)
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

    @AfterEach
    public void clean() {
        itemRepository.deleteAll().block();
    }

    @Test
    public void saveItem() {
        Item item = new Item();
        item.setCount(0);
        item.setPrice(100);
        item.setTitle("Item");
        item.setDescription("Description");
        System.out.println("Before safe "+ item.toString());
        Item saveItem = itemRepository.save(item).block();
        Assertions.assertEquals(item,saveItem);
        System.out.println(saveItem.toString());
        System.out.println("After safe" + item.toString());
    }

    @Test
    public void deleteItem() {
        Item item1 = itemRepository.save(item).block();
        itemRepository.deleteById(item1.getId()).block();
        List<Item> items = itemRepository.findAll().collectList().block();
        Assertions.assertEquals(0,items.size());
    }

    @Test
    public void findByGetId(){
        Item item1 = itemRepository.save(item).block();
        Item findItem = itemRepository.findById(item1.getId()).block();
        Assertions.assertNotNull(findItem);
    }
}
