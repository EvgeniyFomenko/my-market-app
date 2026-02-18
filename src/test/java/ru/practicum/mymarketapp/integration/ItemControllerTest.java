package ru.practicum.mymarketapp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.repository.ItemRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MockMvc mockMvc;
    private Item item;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setCount(1);
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        itemRepository.save(item);
    }

    @AfterEach
    public void after(){
        itemRepository.deleteById(item.getId());
    }

    @Test
    public void getItem() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<h5 class=\"card-title\">Auto</h5>")))
                .andExpect(content().string(containsString("<p class=\"card-text\">Description</p>")));
    }

    @Test
    public void postItem() throws Exception {
        mockMvc.perform(post("/items").param("id", item.getId().toString())
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<h5 class=\"card-title\">Auto</h5>")))
                .andExpect(content().string(containsString("<p class=\"card-text\">Description</p>")))
                .andExpect(content().string(containsString("<span>1</span>")));
    }
    @Test
    public void getItemById() throws Exception{
        mockMvc.perform(get("/items/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<h5 class=\"card-title\">Auto</h5>")))
                .andExpect(content().string(containsString("<p class=\"card-text\">Description</p>")))
                .andExpect(content().string(containsString("<span>1</span>")));
    }

}
