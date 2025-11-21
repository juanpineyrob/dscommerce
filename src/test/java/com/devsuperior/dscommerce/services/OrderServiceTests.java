package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factories.OrderFactory;
import com.devsuperior.dscommerce.factories.ProductFactory;
import com.devsuperior.dscommerce.factories.UserFactory;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    private Order order;

    private OrderDTO orderDTO;

    private Product product;

    private  User userClient,
                userAdmin;

    private Long existingOrderId,
                nonExistingOrderId,
                existingProductId,
                nonExistingProductId;

    @BeforeEach
    void setUp() {
        existingOrderId = 1L;
        nonExistingOrderId = 2L;
        existingProductId = 1L;
        nonExistingProductId = 2L;

        order = OrderFactory.createOrder();

        orderDTO = new OrderDTO(order);

        product = ProductFactory.createProductWithDefaultParameters();

        userClient = UserFactory.createRoleClientUser("user@ds.com");
        userAdmin = UserFactory.createRoleAdminUser("admin@ds.com");

        Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);

        Mockito.when(orderItemRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(Mockito.any());

        OrderDTO orderDTO = orderService.findById(existingOrderId);

        Assertions.assertNotNull(orderDTO);

        Assertions.assertEquals(orderDTO.getId(), existingOrderId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists()  {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(Mockito.any());

        Throwable exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            orderService.findById(nonExistingOrderId);
        });

        Assertions.assertEquals("Orden no encontrada", exception.getMessage());
    }

    @Test
    void findByIdShouldThrowForbiddenExceptionWhenIdDoesNotSelfOrAdmin() {
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(Mockito.any());

        Throwable exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            orderService.findById(existingOrderId);
        });
    }

    @Test
    void insertShouldReturnOrderDTOWhenClientLogged() {
        Mockito.when(userService.authenticated()).thenReturn(userClient);

        OrderItem orderItem = new OrderItem(order, product, 2, 100.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        OrderDTO result = orderService.insert(orderDTO);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    void insertShouldReturnOrderDTOWhenAdminLogged() {
        Mockito.when(userService.authenticated()).thenReturn(userAdmin);

        OrderDTO result = orderService.insert(orderDTO);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    void insertShouldThrowUsernameNotFoundExceptionWhenUserNotLogged() {
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
           orderService.insert(orderDTO);
        });
    }

    @Test
    void insertShouldThrowEntityNotFoundExceptionWhenOrderProductIdDoesNotExist() {
        Mockito.when(userService.authenticated()).thenReturn(userClient);

        product.setId(nonExistingProductId);

        OrderItem orderItem = new OrderItem(order, product, 2, 100.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderService.insert(orderDTO);
        });
    }
}

