package ru.practicum.mymarketapp.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.ItemRepository;
import ru.practicum.mymarketapp.service.CartItemCountService;
import ru.practicum.mymarketapp.service.ItemService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @MockitoBean
    private ItemRepository itemRepository;
    @MockitoBean
    private CartItemCountRepository cartItemCountRepository;

    CartItemCount cartItemCount;
    Order order;
    Item item;
    @Autowired
    private CartItemCountService cartItemCountService;

    @BeforeEach
    void setUp() {
        reset(itemRepository);
        order = new Order();
        order.setTotal(new BigDecimal(100));
        order.setPaid(false);
        item = new Item();
        item.setId(1L);
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Title");
        item.setDescription("Description");
        cartItemCount = new CartItemCount();
        cartItemCount.setId(1L);
        cartItemCount.setItemId(item);
        cartItemCount.setOrderId(order);
    }

    @Test
    public void getItems() {
        Mockito.doReturn(List.of(item)).when(itemRepository).findAll();
        List<Item> cartItemCountFind = itemService.getItems();
        assertEquals(1, cartItemCountFind.size());
    }

    @Test
    public void saveItem() {
        Mockito.doReturn(item).when(itemRepository).save(item);
        itemService.saveItem(item);
    }

    @Test
    public void updateItem(){
        Mockito.doReturn(item).when(itemRepository).save(item);
        itemService.updateItem(item);
    }

    @Test
    public void findById() {
        Mockito.doReturn(Optional.of(item)).when(itemRepository).findById(item.getId());
        itemService.findById(item.getId());
    }

    @Test
    public void findByOrderAndItemId() {
        Mockito.doReturn(List.of(cartItemCount)).when(cartItemCountRepository).findByOrderId(order);
        itemService.findByOrderAndItemId(order, item.getId());
    }


}
