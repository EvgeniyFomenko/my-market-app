package ru.practicum.mymarketapp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.practicum.mymarketapp.PostgresqlTestContainer;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.pojo.Action;
import ru.practicum.mymarketapp.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
@ImportTestcontainers(PostgresqlTestContainer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ItemControllerTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private WebTestClient mockMvc;
    private Item item;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setPrice(100);
        item.setTitle("Auto");
        item.setDescription("Description");
        item = itemRepository.save(item).block();
    }

    @AfterEach
    public void after(){
        itemRepository.deleteById(item.getId()).subscribe();
    }

    @Test
    public void getItem() {
        mockMvc.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                });
    }

    @Test
    public void postItem() throws Exception {
        mockMvc.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                });


        mockMvc.post().uri(uribuilder ->uribuilder.path("/items").build()).body(BodyInserters.fromFormData("action", Action.PLUS.getFullName())
                        .with("id", item.getId().toString())
                        .with("search","")
                        .with("sort","NO")
                        .with("pageSize","5")
                        .with("pageNumber","1"))
                .exchange()
                .expectStatus().is3xxRedirection();

        mockMvc.get().uri(uriBuilder -> uriBuilder.path("/items").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                    assertTrue(body.contains("<span>1</span>"));
                });

    }
    @Test
    public void getItemById() throws Exception{
        mockMvc.get().uri("/items/" + item.getId().toString())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                    assertTrue(body.contains("<span>0</span>"));

                });
    }

    @Test
    public void postItemById() throws Exception{
        mockMvc.post().uri(uriBuilder -> uriBuilder.path("/items/" + item.getId().toString()).build() ).body(BodyInserters.fromFormData("action", Action.PLUS.getFullName()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h5 class=\"card-title\">Auto</h5>"));
                    assertTrue(body.contains("<p class=\"card-text\">Description</p>"));
                    assertTrue(body.contains("<span>1</span>"));

                });
    }

}
