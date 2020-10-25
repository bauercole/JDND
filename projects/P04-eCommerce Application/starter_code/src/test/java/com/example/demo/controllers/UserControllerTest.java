package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void create_user_bad_request_different_passwords(){
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("differentPassword");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_bad_request_short_password(){
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("pass");
        request.setConfirmPassword("pass");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void verify_findById(){
        String userName = "findById";
        String password = "FBIPassword";
        String encodedPassword = "encodedFBIPassword";

        when(encoder.encode(password)).thenReturn(encodedPassword);
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(userName);
        request.setPassword(password);
        request.setConfirmPassword(password);
        ResponseEntity<User> savedCustomerResponse = userController.createUser(request);
        when(userRepository.findById(savedCustomerResponse.getBody().getId())).thenReturn(Optional.of(savedCustomerResponse.getBody()));

        ResponseEntity<User> foundUser = userController.findById(savedCustomerResponse.getBody().getId());

        assertNotNull(foundUser);
        assertEquals(userName, foundUser.getBody().getUsername());
        assertEquals(encodedPassword, foundUser.getBody().getPassword());
    }

    @Test
    public void verify_findByName(){
        String userName = "findByName";
        String password = "FBUNPassword";
        String encodedPassword = "encodedFBUNPassword";

        when(encoder.encode(password)).thenReturn(encodedPassword);
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(userName);
        request.setPassword(password);
        request.setConfirmPassword(password);
        ResponseEntity<User> savedCustomerResponse = userController.createUser(request);
        when(userRepository.findByUsername(userName)).thenReturn(savedCustomerResponse.getBody());

        ResponseEntity<User> foundUser = userController.findByUserName(userName);

        assertNotNull(foundUser);
        assertEquals(userName, foundUser.getBody().getUsername());
        assertEquals(encodedPassword, foundUser.getBody().getPassword());
    }

}
