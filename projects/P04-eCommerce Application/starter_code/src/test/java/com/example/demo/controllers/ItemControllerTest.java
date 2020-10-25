package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void Setup(){
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems_happyPath(){
        Item item1 = new Item();
        item1.setId(1l);
        item1.setDescription("firstItemDescription");
        item1.setName("firstItemName");
        item1.setPrice(BigDecimal.valueOf(1.00));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("secondItemDescription");
        item2.setName("secondItemName");
        item2.setPrice(BigDecimal.valueOf(2.22));

        List<Item> itemsList = new ArrayList<>();
        itemsList.add(item1);
        itemsList.add(item2);

        when(itemRepository.findAll()).thenReturn(itemsList);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemsList, response.getBody());
    }

    @Test
    public void getItemById_happyPath(){
        Item item1 = new Item();
        item1.setId(1L);
        item1.setDescription("firstItemDescription");
        item1.setName("firstItemName");
        item1.setPrice(BigDecimal.valueOf(1.00));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item1, response.getBody());
    }

    @Test
    public void getItemById_itemNotFound(){
        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getItemsByName_happyPath(){
        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("secondItemDescription");
        item2.setName("secondItemName");
        item2.setPrice(BigDecimal.valueOf(2.22));

        when(itemRepository.findByName("secondItemName")).thenReturn(Arrays.asList(item2));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("secondItemName");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().size());
        assertEquals(item2, response.getBody().get(0));
    }

}
