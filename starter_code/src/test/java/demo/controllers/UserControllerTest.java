package demo.controllers;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Customer;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import demo.TestUtils;
import org.junit.Before;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

    }

    @Test
    public void create_user_happy_path() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        final ResponseEntity<Customer> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Customer user = response.getBody();
        assertNotNull(user);
        assertNull(null, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void testPasswordLengthLessThan7() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("user");
        r.setPassword("pw");
        r.setConfirmPassword("pw");
        final ResponseEntity<Customer> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());

    }

    @Test
    public void testInvalidConfirmPassword() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("Password");
        final ResponseEntity<Customer> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());

    }

    @Test
    public void testFindByUserNameSucceed() {
        Customer user = new Customer();
        String username = "test";
        user.setUsername(username);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final ResponseEntity<Customer> responseHappy = userController.findByUserName(username);
        assertEquals(200, responseHappy.getStatusCode().value());

        final ResponseEntity<Customer> responseBad = userController.findByUserName("testtest");
        assertEquals(HttpStatus.NOT_FOUND, responseBad.getStatusCode()); //404

    }

    @Test
    public void testFindByUserNameFailed() {
        String username = "test";
        when(userRepository.findByUsername(username)).thenReturn(null);
        final ResponseEntity<Customer> responseBad = userController.findByUserName(username);
        assertEquals(HttpStatus.NOT_FOUND, responseBad.getStatusCode()); //404

    }

    @Test
    public void testFindByIdSucceed() {
        Customer user = new Customer();
        Long Id = 1L;
        user.setId(Id);
        when(userRepository.findById(Id)).thenReturn(Optional.of(user));

        final ResponseEntity<Customer> responseHappy = userController.findById(Id);
        assertEquals(200, responseHappy.getStatusCode().value());
    }

    @Test
    public void testFindByIdFailed() {
        Long Id = 999L;
        when(userRepository.findById(Id)).thenReturn(Optional.empty());
        final ResponseEntity<Customer> response = userController.findById(Id);
        assertEquals(404, response.getStatusCode().value());
    }


}
