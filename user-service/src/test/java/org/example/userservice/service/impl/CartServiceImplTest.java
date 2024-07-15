package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.communication.ProductServiceCommunicator;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.dto.product.PriceDto;
import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.exception.CartItemNotFoundException;
import org.example.userservice.exception.CartNotFoundException;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.mapper.CartContentMapper;
import org.example.userservice.model.Cart;
import org.example.userservice.model.CartItem;
import org.example.userservice.repository.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID cartId = UUID.randomUUID();
    private final UUID cartItemId = UUID.randomUUID();
    private final Jwt jwt = mockJwt();
    private final UpdateQuantityRequest quantityRequest = new UpdateQuantityRequest(10);

    @Mock
    private CartContentMapper mapper;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductServiceCommunicator communicator;

    @InjectMocks
    private CartServiceImpl service;

    private CartItemRequest cartItemRequest;
    private CartContentResponse cartContentResponse;
    private ProductDetailsDto productDetails;
    private HttpServletResponse httpResponseMock;

    @BeforeEach
    void setUp() {

        final int id = 1;
        final String name = "name";
        final String description = "description";
        final String brand = "brand";
        final String countryManufacturer = "country";
        final BigDecimal priceAmount = new BigDecimal("123.456");
        final String currency = "UAH";
        final Double lengthInMeters = 1.0;
        final Double widthInMeters = 1.0;
        final Double heightInMeters = 1.0;
        final Double netWeightInKg = 1.0;
        final Double grossWeightInKg = 1.0;

        productDetails = new ProductDetailsDto(
                id,
                name,
                description,
                brand,
                countryManufacturer,
                Collections.emptySet(),
                new PriceDto(priceAmount, currency),
                Collections.emptyList(),
                lengthInMeters,
                widthInMeters,
                heightInMeters,
                netWeightInKg,
                grossWeightInKg
        );

        final int quantity = 10;
        cartItemRequest = new CartItemRequest(id, quantity);
        CartItemResponse cartItemResponse = new CartItemResponse(cartItemId, productDetails, quantity, priceAmount.multiply(BigDecimal.valueOf(quantity)));
        cartContentResponse = new CartContentResponse(
                new ArrayList<>(List.of(cartItemResponse)),
                Map.of(currency, priceAmount.multiply(BigDecimal.valueOf(quantity))),
                quantity
        );

        httpResponseMock = mock(HttpServletResponse.class);
    }

    @Test
    void addItemToCart_ShouldReturnCreatedCartWithOneItem_WhenNotAuthenticatedAndCartIdNotSpecified() {

        when(communicator.getProductById(cartItemRequest.productId()))
                .thenReturn(productDetails);

        Cart stubbedCart = new Cart(cartId, null, new ArrayList<>());

        when(cartRepository.save(any(Cart.class))).thenReturn(stubbedCart);
        when(mapper.toResponse(stubbedCart)).thenReturn(cartContentResponse);

        CartContentResponse result = service.addItemToCart(null, cartItemRequest, null, httpResponseMock);

        verify(httpResponseMock).addCookie(any(Cookie.class));
        assertEquals(cartContentResponse, result);
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieExc_WhenCartNotFoundByCookieAndNotAuthenticated() {

        when(communicator.getProductById(cartItemRequest.productId()))
                .thenReturn(productDetails);

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(null, cartItemRequest, cartId, httpResponseMock)
        );
    }

    @Test
    void addItemToCart_ShouldAddCookieAndReturnCartContentWithCreatedItem_WhenNotAuthenticatedAndCartIdSpecified() {

        when(communicator.getProductById(cartItemRequest.productId()))
                .thenReturn(productDetails);

        Cart stubbedCart = new Cart(cartId, null, new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(stubbedCart)).thenReturn(cartContentResponse);
        when(cartRepository.save(stubbedCart)).thenReturn(stubbedCart);

        CartContentResponse result = service.addItemToCart(null, cartItemRequest, cartId, httpResponseMock);

        assertAll(
                () -> assertEquals(1, stubbedCart.getItems().size()),
                () -> assertEquals(cartContentResponse, result)
        );

        verify(httpResponseMock).addCookie(any(Cookie.class));
        verify(cartRepository).save(stubbedCart);
    }

    @Test
    void addItemToCart_ShouldCreateAndReturnNewCartWithCartItem_WhenAuthenticatedAndNoCartAssignedAndNoCookieSpecified() {

        when(communicator.getProductById(cartItemRequest.productId()))
                .thenReturn(productDetails);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toResponse(any(Cart.class))).thenReturn(cartContentResponse);

        CartContentResponse result = service.addItemToCart(jwt, cartItemRequest, null, httpResponseMock);

        verify(cartRepository).save(any(Cart.class));
        assertEquals(cartContentResponse, result);
    }

    @Test
    void addItemToCart_ShouldAddItemToCartAndReturnCartContent_WhenAuthenticatedAndCartIsAssignedAndNoCookieSpecified() {

        when(communicator.getProductById(cartItemRequest.productId()))
                .thenReturn(productDetails);

        Cart stubbedCart = new Cart(cartId, userId, new ArrayList<>());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(stubbedCart)).thenReturn(cartContentResponse);

        CartContentResponse result = service.addItemToCart(jwt, cartItemRequest, null, httpResponseMock);

        verify(cartRepository).save(stubbedCart);

        assertAll(
                () -> assertEquals(1, stubbedCart.getItems().size()),
                () -> assertEquals(cartContentResponse, result)
        );
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieExc_WhenCartIsAlreadyAssignedAndCookieIdSpecified() {

        Cart stubbedCart = new Cart(cartId, userId, new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(jwt, cartItemRequest, cartId, httpResponseMock)
        );

        verify(cartRepository, never()).save(stubbedCart);
    }

    @Test
    void addItemToCart_ShouldAssignCartAndDeleteCookieAndAddItemToCart_WhenAuthenticatedAndCookieSpecified() {

        when(communicator.getProductById(cartItemRequest.productId()))
                .thenReturn(productDetails);

        Cart stubbedCart = new Cart(cartId, null, new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(cartRepository.findByUserId(userId)).thenAnswer(invocationOnMock -> {
            UUID requestedCartUserId = invocationOnMock.getArgument(0);
            if (stubbedCart.getUserId().equals(requestedCartUserId)) {
                return Optional.of(stubbedCart);
            }

            Assertions.fail("Cart is not assigned to user correctly");
            throw new IllegalStateException("This line should never be reached");
        });

        when(mapper.toResponse(stubbedCart)).thenReturn(cartContentResponse);

        CartContentResponse result = service.addItemToCart(jwt, cartItemRequest, cartId, httpResponseMock);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponseMock).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();
        assertAll(
                () -> assertNotNull(passedCookie),
                () -> assertEquals(0, passedCookie.getMaxAge()),
                () -> assertEquals(1, stubbedCart.getItems().size()),
                () -> assertEquals(cartContentResponse, result)
        );

        verify(cartRepository, times(2)).save(stubbedCart);
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdCookieExc_WhenNotAuthenticatedAndCookieIdNull() {
        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(null, cartItemId, quantityRequest,cartId, httpResponseMock)
        );
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdCookieExc_WhenNotAuthenticatedCartNotFoundByIdFromCookie() {

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(null, cartItemId, quantityRequest,cartId, httpResponseMock)
        );
    }

    @Test
    void updateItemQuantity_ShouldThrowCartNotFoundExc_WhenAuthenticatedAndCookieIsNull() {

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> service.updateItemQuantity(jwt, cartItemId, quantityRequest, null, httpResponseMock)
        );

        verify(cartRepository, never()).findById(any(UUID.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdExc_WhenAuthenticatedAndCartNotFoundByIdFromCookie() {

        Cart stubbedCart = new Cart(cartId, userId, new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(jwt, cartItemId, quantityRequest, cartId, httpResponseMock)
        );

        verify(cartRepository, never()).save(any(Cart.class));
        verify(httpResponseMock, never()).addCookie(any(Cookie.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdExc_WhenAuthenticatedAndFoundCartAlreadyAssigned() {

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(jwt, cartItemId, quantityRequest, cartId, httpResponseMock)
        );

        verify(cartRepository, never()).save(any(Cart.class));
        verify(httpResponseMock, never()).addCookie(any(Cookie.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowCartItemNotFoundExc_WhenItemNotFoundInCart() {

        Cart stubbedCart = new Cart(cartId, null, new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                CartItemNotFoundException.class,
                () -> service.updateItemQuantity(null, cartItemId, quantityRequest, cartId, httpResponseMock)
        );

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateItemQuantity_ShouldReturnUpdatedItem_WhenNotAuthenticated() {

        CartItem itemToBeUpdated = new CartItem(cartItemId, productDetails, 9);
        Cart stubbedCart = new Cart(cartId, null, Collections.singletonList(itemToBeUpdated));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        service.updateItemQuantity(null, cartItemId, quantityRequest, cartId, httpResponseMock);

        assertAll(
                () -> assertTrue(stubbedCart.getItems().contains(itemToBeUpdated)),
                () -> assertEquals(cartItemRequest.quantity(), itemToBeUpdated.getQuantity())
        );

        verify(mapper).toResponse(stubbedCart);
    }

    @Test
    void updateItemQuantity_ShouldReturnUpdatedItemAndDeleteCookie_WhenAuthenticatedAndCookieSpecified() {

        CartItem itemToBeUpdated = new CartItem(cartItemId, productDetails, 9);
        Cart stubbedCart = new Cart(cartId, null, Collections.singletonList(itemToBeUpdated));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));

        service.updateItemQuantity(jwt, cartItemId, quantityRequest,cartId, httpResponseMock);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponseMock, times(1)).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();

        assertAll(
                () -> assertNotNull(passedCookie),
                () -> assertEquals(0, passedCookie.getMaxAge()),
                () -> assertTrue(stubbedCart.getItems().contains(itemToBeUpdated)),
                () -> assertEquals(cartItemRequest.quantity(), itemToBeUpdated.getQuantity())
        );

        verify(mapper).toResponse(stubbedCart);
    }

    private Jwt mockJwt() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId.toString());
        return jwt;
    }
}