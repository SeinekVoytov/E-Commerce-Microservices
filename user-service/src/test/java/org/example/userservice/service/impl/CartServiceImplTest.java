package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.dto.product.PriceDto;
import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.exception.CartItemNotFoundException;
import org.example.userservice.exception.CartNotFoundException;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.exception.ProductNotFoundException;
import org.example.userservice.mapper.cart.CartContentMapper;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.Price;
import org.example.userservice.model.product.Product;
import org.example.userservice.model.product.ProductDetails;
import org.example.userservice.repository.cart.CartItemRepository;
import org.example.userservice.repository.cart.CartRepository;
import org.example.userservice.repository.product.ProductDetailsRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID cartId = UUID.randomUUID();
    private final Jwt jwt = mockJwt();
    private final UpdateQuantityRequest quantityRequest = new UpdateQuantityRequest(10);
    private final int itemId = 1;

    @Mock
    private CartContentMapper mapper;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private CartItemRepository cartItemRepo;

    @Mock
    private ProductDetailsRepository productDetailsRepo;

    @InjectMocks
    private CartServiceImpl service;

    private CartItemRequest cartItemRequest;
    private CartContentResponse response;
    private ProductDetails productDetails;
    private HttpServletResponse httpResponseMock;

    @BeforeEach
    void setUp() {

        final int id = 1;
        final String name = "name";
        final String description = "description";
        final BigDecimal priceAmount = new BigDecimal("123.456");
        final Currency currency = Currency.getInstance("USD");
        final Double lengthInMeters = 1.0;
        final Double widthInMeters = 1.0;
        final Double heightInMeters = 1.0;
        final Double netWeightInKg = 1.0;
        final Double grossWeightInKg = 1.0;

        ProductDetailsDto productDto = new ProductDetailsDto(
                id,
                name,
                description,
                Collections.emptySet(),
                new PriceDto(priceAmount, currency),
                Collections.emptySet(),
                lengthInMeters,
                widthInMeters,
                heightInMeters,
                netWeightInKg,
                grossWeightInKg
        );

        final int quantity = 10;
        cartItemRequest = new CartItemRequest(id, quantity);
        CartItemResponse cartItemResponse = new CartItemResponse(id, productDto, quantity);
        response = new CartContentResponse(
                new ArrayList<>(List.of(cartItemResponse)),
                Map.of(currency, priceAmount.multiply(BigDecimal.valueOf(quantity)))
        );

        Product product = new Product(
                id,
                name,
                netWeightInKg,
                description,
                Collections.emptySet(),
                new Price(id, priceAmount, currency),
                Collections.emptySet()
        );

        productDetails = new ProductDetails(
                id,
                product,
                lengthInMeters,
                widthInMeters,
                heightInMeters,
                grossWeightInKg
        );

        httpResponseMock = mock(HttpServletResponse.class);
    }

    @Test
    void addItemToCart_ShouldThrowProductNotFoundExc_WhenProductNotFoundAndNotAuthenticated() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addItemToCart(null, cartItemRequest, cartId, httpResponseMock)
        );
    }

    @Test
    void addItemToCart_ShouldThrowProductNotFoundExc_WhenProductNotFoundAndAuthenticated() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addItemToCart(jwt, cartItemRequest, null, httpResponseMock)
        );
    }

    @Test
    void addItemToCart_ShouldReturnCreatedCartWithOneItem_WhenNotAuthenticatedAndCartIdNotSpecified() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.of(productDetails));

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .build();

        when(cartRepo.save(any(Cart.class))).thenReturn(stubbedCart);
        when(mapper.toResponse(stubbedCart)).thenReturn(response);

        CartContentResponse result = service.addItemToCart(null, cartItemRequest, null, httpResponseMock);

        verify(httpResponseMock, times(1)).addCookie(any(Cookie.class));
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieExc_WhenCartNotFoundByCookieAndNotAuthenticated() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.of(productDetails));
        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(null, cartItemRequest, cartId, httpResponseMock)
        );
    }

    @Test
    void addItemToCart_ShouldAddCookieAndReturnCartContentWithCreatedItem_WhenNotAuthenticatedAndCartIdSpecified() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.of(productDetails));

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(stubbedCart)).thenReturn(response);
        when(cartRepo.save(stubbedCart)).thenReturn(stubbedCart);

        CartContentResponse result = service.addItemToCart(null, cartItemRequest, cartId, httpResponseMock);

        assertAll(
                () -> assertEquals(1, stubbedCart.getItems().size()),
                () -> assertEquals(response, result)
        );

        verify(httpResponseMock, times(1)).addCookie(any(Cookie.class));
        verify(cartRepo, times(1)).save(stubbedCart);
    }

    @Test
    void addItemToCart_ShouldCreateAndReturnNewCartWithCartItem_WhenAuthenticatedAndNoCartAssignedAndNoCookieSpecified() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.of(productDetails));

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toResponse(any(Cart.class))).thenReturn(response);

        CartContentResponse result = service.addItemToCart(jwt, cartItemRequest, null, httpResponseMock);

        verify(cartRepo, times(1)).save(any(Cart.class));
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldAddItemToCartAndReturnCartContent_WhenAuthenticatedAndCartIsAssignedAndNoCookieSpecified() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.of(productDetails));

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(stubbedCart)).thenReturn(response);

        CartContentResponse result = service.addItemToCart(jwt, cartItemRequest, null, httpResponseMock);

        verify(cartRepo, times(1)).save(stubbedCart);

        assertAll(
                () -> assertEquals(1, stubbedCart.getItems().size()),
                () -> assertEquals(response, result)
        );
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieExc_WhenCartIsAlreadyAssignedAndCookieIdSpecified() {

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(jwt, cartItemRequest, cartId, httpResponseMock)
        );

        verify(cartRepo, never()).save(stubbedCart);
    }

    @Test
    void addItemToCart_ShouldAssignCartAndDeleteCookieAndAddItemToCart_WhenAuthenticatedAndCookieSpecified() {

        when(productDetailsRepo.findById(cartItemRequest.productId())).thenReturn(Optional.of(productDetails));

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(cartRepo.findByUserId(userId)).thenAnswer(invocationOnMock -> {
            UUID requestedCartUserId = invocationOnMock.getArgument(0);
            if (stubbedCart.getUserId().equals(requestedCartUserId)) {
                return Optional.of(stubbedCart);
            }

            Assertions.fail("Cart is not assigned to user correctly");
            throw new IllegalStateException("This line should never be reached");
        });

        when(mapper.toResponse(stubbedCart)).thenReturn(response);

        CartContentResponse result = service.addItemToCart(jwt, cartItemRequest, cartId, httpResponseMock);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponseMock, times(1)).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();
        assertAll(
                () -> assertNotNull(passedCookie),
                () -> assertEquals(0, passedCookie.getMaxAge()),
                () -> assertEquals(1, stubbedCart.getItems().size()),
                () -> assertEquals(response, result)
        );

        verify(cartRepo, times(2)).save(stubbedCart);
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdCookieExc_WhenNotAuthenticatedAndCookieIdNull() {
        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(null, itemId, quantityRequest,cartId, httpResponseMock)
        );
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdCookieExc_WhenNotAuthenticatedCartNotFoundByIdFromCookie() {

        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(null, itemId, quantityRequest,cartId, httpResponseMock)
        );
    }

    @Test
    void updateItemQuantity_ShouldThrowCartNotFoundExc_WhenAuthenticatedAndCookieIsNull() {

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> service.updateItemQuantity(jwt, itemId, quantityRequest, null, httpResponseMock)
        );

        verify(cartRepo, never()).findById(any(UUID.class));
        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdExc_WhenAuthenticatedAndCartNotFoundByIdFromCookie() {

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(jwt, itemId, quantityRequest, cartId, httpResponseMock)
        );

        verify(cartRepo, never()).save(any(Cart.class));
        verify(httpResponseMock, never()).addCookie(any(Cookie.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdExc_WhenAuthenticatedAndFoundCartAlreadyAssigned() {

        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(jwt, itemId, quantityRequest, cartId, httpResponseMock)
        );

        verify(cartRepo, never()).save(any(Cart.class));
        verify(httpResponseMock, never()).addCookie(any(Cookie.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowCartItemNotFoundExc_WhenItemNotFoundInCart() {

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(null)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                CartItemNotFoundException.class,
                () -> service.updateItemQuantity(null, itemId, quantityRequest, cartId, httpResponseMock)
        );

        verify(cartItemRepo, never()).save(any(CartItem.class));
    }

    @Test
    void updateItemQuantity_ShouldReturnUpdatedItem_WhenNotAuthenticated() {

        CartItem itemToBeUpdated = new CartItem(1, productDetails, 9);
        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(null)
                .items(Collections.singletonList(itemToBeUpdated))
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        service.updateItemQuantity(null, itemId, quantityRequest, cartId, httpResponseMock);

        assertAll(
                () -> assertTrue(stubbedCart.getItems().contains(itemToBeUpdated)),
                () -> assertEquals(cartItemRequest.quantity(), itemToBeUpdated.getQuantity())
        );

        verify(mapper, times(1)).toResponse(stubbedCart);
    }

    @Test
    void updateItemQuantity_ShouldReturnUpdatedItemAndDeleteCookie_WhenAuthenticatedAndCookieSpecified() {

        CartItem itemToBeUpdated = new CartItem(1, productDetails, 9);
        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(null)
                .items(Collections.singletonList(itemToBeUpdated))
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));

        service.updateItemQuantity(jwt, itemId, quantityRequest,cartId, httpResponseMock);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponseMock, times(1)).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();

        assertAll(
                () -> assertNotNull(passedCookie),
                () -> assertEquals(0, passedCookie.getMaxAge()),
                () -> assertTrue(stubbedCart.getItems().contains(itemToBeUpdated)),
                () -> assertEquals(cartItemRequest.quantity(), itemToBeUpdated.getQuantity())
        );

        verify(mapper, times(1)).toResponse(stubbedCart);
    }

    private Jwt mockJwt() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId.toString());
        return jwt;
    }
}