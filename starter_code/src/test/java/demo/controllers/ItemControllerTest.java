package demo.controllers;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import demo.TestUtils;
import org.junit.Before;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ItemControllerTest {
    private ItemController itemController;

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private List<Item> items;

    @Before
    public void setUp() {
        itemController = new ItemController(itemRepository);
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        items = new ArrayList<>();
        Item round = new Item("Round Widget", 2.99, "A widget that is round");
        Item square = new Item("Square Widget", 1.99, "A widget that is square");
        items.add(round);
        items.add(square);
    }

    @Test
    public void testGetItems() {
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals(2.99, response.getBody().get(0).getPrice());
        assertEquals(1.99, response.getBody().get(1).getPrice());
    }

    @Test
    public void testGetItemById() {
        when(itemRepository.findById(items.get(0).getId())).thenReturn(Optional.ofNullable(items.get(0)));
        ResponseEntity<Item> response = itemController.getItemById(items.get(0).getId());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(items.get(0).getName(), Objects.requireNonNull(response.getBody()).getName());
        assertEquals(items.get(0).getPrice(), response.getBody().getPrice());
        assertEquals(items.get(0).getDescription(), response.getBody().getDescription());
        ResponseEntity<Item> responseBad = itemController.getItemById(3L);
        assertEquals(404, responseBad.getStatusCode().value());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> itemsByName = new ArrayList<>();
        itemsByName.add(items.get(0));
        itemsByName.add(items.get(1));

        when(itemRepository.findByName(items.get(0).getName())).thenReturn(itemsByName);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(items.get(0).getName());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Round Widget", Objects.requireNonNull(response.getBody()).get(0).getName());
        assertEquals("Square Widget", response.getBody().get(1).getName());

        ResponseEntity<List<Item>> responseBad = itemController.getItemsByName("Triangle Widget");
        assertEquals(404, responseBad.getStatusCode().value());
    }
}
