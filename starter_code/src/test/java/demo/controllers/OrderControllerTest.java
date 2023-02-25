package demo.controllers;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Customer;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private List<Item> itemList;

    @Before
    public void setUp() {
        orderController = new OrderController(userRepository, orderRepository);
        itemList = List.of(
                new Item("Round Widget", 2.99, "A widget that is round"),
                new Item("Square Widget", 1.99, "A widget that is square"));
    }

    @Test
    public void testOrderSubmitSucceed() {
        Customer user = createUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(response.getBody()).getItems().size());
        assertEquals(4.98, response.getBody().getTotal().doubleValue()); //2.99 + 1.99
    }

    @Test
    public void testOrderSubmitUserNotFound() {
        String username = "userNotFound";
        when(userRepository.findByUsername(username)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(username);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testGetOrderForUserSucceed() {
        Customer user = createUser();
        List<UserOrder> orderList = new ArrayList<>();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        orderList.add(UserOrder.createFromCart(user.getCart()));
        when(orderRepository.findByCustomer(user)).thenReturn(orderList);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(4.98, response.getBody().get(0).getTotal().doubleValue());
        assertEquals("Round Widget", response.getBody().get(0).getItems().get(0).getName());
        assertEquals("Square Widget", response.getBody().get(0).getItems().get(1).getName());
    }

    @Test
    public void testGetOrderUserNotFound() {
        String username = "userNotFound";
        when(userRepository.findByUsername(username)).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);
        assertEquals(404, response.getStatusCode().value());
    }

    private Customer createUser() {
        Customer user = new Customer();
        user.setUsername("Xin");
        user.setPassword("password");

        Cart cart = new Cart();
        cart.addItem(itemList.get(0));
        cart.addItem(itemList.get(1));
        user.setCart(cart);
        return user;
    }

}



