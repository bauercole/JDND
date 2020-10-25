package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void verify_addToCart_happyPath(){
        String username = "testUser";
        Cart cart = new Cart();


        User user = new User();
        user.setUsername(username);
        user.setPassword("testPassword");
        user.setCart(cart);
        cart.setUser(user);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("testDescription");
        item.setName("testName");
        item.setPrice(BigDecimal.valueOf(3.00));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().getUser().getUsername());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(1L, response.getBody().getItems().get(0).getId().longValue());
        assertEquals(1L, response.getBody().getItems().get(1).getId().longValue());

    }

    @Test
    public void verify_addToCart_userNotFound(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_addToCart_itemNotFound(){
        String username = "testUser";
        Cart cart = new Cart();

        User user = new User();
        user.setUsername(username);
        user.setPassword("testPassword");
        user.setCart(cart);
        cart.setUser(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_removeFromCart_happyPath(){
        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(3.00));
        item.setName("testName");
        item.setDescription("testDescription");
        item.setId(1L);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemList.add(item);

        String username = "testUser";
        Cart cart = new Cart();
        cart.setItems(itemList);

        User user = new User();
        user.setUsername(username);
        user.setPassword("testPassword");
        user.setCart(cart);
        cart.setUser(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getItems().size());
    }

    @Test
    public void verify_removeFromCart_userNotFound(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_removeFromCart_itemNotFound(){
        String username = "testUser";
        Cart cart = new Cart();

        User user = new User();
        user.setUsername(username);
        user.setPassword("testPassword");
        user.setCart(cart);
        cart.setUser(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
