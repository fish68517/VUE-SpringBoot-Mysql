package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.CartDTO;
import com.shenyang.musicfestival.entity.Product;
import com.shenyang.musicfestival.repository.ProductRepository;
import com.shenyang.musicfestival.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CartService
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    private CartService cartService;

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(productRepository);
    }

    @Test
    void testAddToCart_Success() {
        // Arrange
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(10)
            .isActive(true)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        // Act
        CartDTO cart = cartService.addToCart(USER_ID, PRODUCT_ID, 2);

        // Assert
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getTotalQuantity());
        assertEquals(BigDecimal.valueOf(199.98), cart.getTotalPrice());
    }

    @Test
    void testAddToCart_ProductNotFound() {
        // Arrange
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cartService.addToCart(USER_ID, PRODUCT_ID, 1)
        );
    }

    @Test
    void testAddToCart_ProductInactive() {
        // Arrange
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(10)
            .isActive(false)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cartService.addToCart(USER_ID, PRODUCT_ID, 1)
        );
    }

    @Test
    void testAddToCart_InsufficientStock() {
        // Arrange
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(5)
            .isActive(true)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cartService.addToCart(USER_ID, PRODUCT_ID, 10)
        );
    }

    @Test
    void testAddToCart_InvalidQuantity() {
        // Arrange
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(10)
            .isActive(true)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cartService.addToCart(USER_ID, PRODUCT_ID, 0)
        );
    }

    @Test
    void testGetCart_Empty() {
        // Act
        CartDTO cart = cartService.getCart(USER_ID);

        // Assert
        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
        assertEquals(0, cart.getTotalQuantity());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
    }

    @Test
    void testRemoveFromCart() {
        // Arrange
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(10)
            .isActive(true)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        CartDTO addedCart = cartService.addToCart(USER_ID, PRODUCT_ID, 2);
        Long cartItemId = addedCart.getItems().get(0).getId();

        // Act
        CartDTO updatedCart = cartService.removeFromCart(USER_ID, cartItemId);

        // Assert
        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getItems().size());
        assertEquals(0, updatedCart.getTotalQuantity());
    }

    @Test
    void testClearCart() {
        // Arrange
        Product product = Product.builder()
            .id(PRODUCT_ID)
            .name("T-Shirt")
            .currentPrice(BigDecimal.valueOf(99.99))
            .stock(10)
            .isActive(true)
            .build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        cartService.addToCart(USER_ID, PRODUCT_ID, 2);

        // Act
        cartService.clearCart(USER_ID);
        CartDTO cart = cartService.getCart(USER_ID);

        // Assert
        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }

}
