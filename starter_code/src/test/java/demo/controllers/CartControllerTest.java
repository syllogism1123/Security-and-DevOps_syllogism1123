package demo.controllers;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Customer;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import demo.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private List<Item> itemList;

    @Before
    public void setUp() {
        cartController = new CartController(userRepository, cartRepository, itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        itemList = List.of(new Item("Round Widget", 2.99, "A widget that is round"),
                new Item("Square Widget", 1.99, "A widget that is square"));

    }

    @Test
    public void testAddToCartSucceed() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("Xin");
        req.setItemId(1L);
        req.setQuantity(2);
        Customer user = createUser();

        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.ofNullable(itemList.get(0)));
        ResponseEntity<Cart> resp = cartController.addToCart(req);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(4, Objects.requireNonNull(resp.getBody()).getItems().size());
        assertEquals(10.96, resp.getBody().getTotal().doubleValue(), 0); // 3 * 2.99 + 1.99

    }

    @Test
    public void testAddToCartUserNotfound() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("userNotFound");
        when(userRepository.findByUsername(req.getUsername())).thenReturn(null);
        ResponseEntity<Cart> resp = cartController.addToCart(req);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    public void testAddToCartItemNotfound() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("Xin");
        req.setItemId(99L);
        req.setQuantity(2);
        Customer user = createUser();
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.empty());
        ResponseEntity<Cart> resp = cartController.addToCart(req);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    public void testRemoveFromCartSucceed() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("Xin");
        req.setItemId(1L);
        req.setQuantity(1);
        Customer user = createUser();

        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.ofNullable(itemList.get(0)));
        ResponseEntity<Cart> resp = cartController.removeFromCart(req);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(resp.getBody()).getItems().size());
        assertEquals(1.99, resp.getBody().getTotal().doubleValue(), 0); //1.99
    }

    @Test
    public void testRemoveFromCartUserNotfound() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("userNotFound");
        when(userRepository.findByUsername(req.getUsername())).thenReturn(null);
        ResponseEntity<Cart> resp = cartController.removeFromCart(req);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    public void testRemoveFromCartItemNotfound() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("Xin");
        req.setItemId(99L);
        req.setQuantity(2);
        Customer user = createUser();
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.empty());
        ResponseEntity<Cart> resp = cartController.removeFromCart(req);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    public void testRemoveFromCartInvalidQuantity() {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("Xin");
        req.setItemId(1L);
        req.setQuantity(3);
        Customer user = createUser();

        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.ofNullable(itemList.get(0)));
        ResponseEntity<Cart> resp = cartController.removeFromCart(req);
        assertEquals(404, resp.getStatusCode().value());
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
