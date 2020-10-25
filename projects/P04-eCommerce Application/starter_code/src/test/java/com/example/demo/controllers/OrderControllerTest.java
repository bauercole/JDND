package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.criteria.Order;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }


/*    */

    @Test
    public void submit_happyPath(){
        String username = "testUser";

        User user = new User();
        user.setUsername(username);
        user.setPassword("testPassword");

        Item item = new Item();
        item.setId(1L);
        item.setDescription("testDescription");
        item.setName("testName");
        item.setPrice(BigDecimal.valueOf(3.00));
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemList.add(item);

        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        cart.setItems(itemList);

        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit(username);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().getUser());
    }

    @Test
    public void submit_userNotFound(){
        ResponseEntity<UserOrder> response = orderController.submit("testUserName");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getOrdersForUser_happyPath(){
        String username = "testUser";

        User user = new User();
        user.setUsername(username);
        user.setPassword("testPassword");

        Item item = new Item();
        item.setId(1L);
        item.setDescription("testDescription");
        item.setName("testName");
        item.setPrice(BigDecimal.valueOf(3.00));
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemList.add(item);

        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        cart.setItems(itemList);

        UserOrder order = new UserOrder();
        order.setItems(itemList);
        order.setUser(user);
        order.setTotal(BigDecimal.valueOf(3.00));

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().size());
        assertEquals(order, response.getBody().get(0));
        assertEquals(user, response.getBody().get(0).getUser());
    }

    @Test
    public void getOrdersForUser_userNotFound(){
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUserName");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
