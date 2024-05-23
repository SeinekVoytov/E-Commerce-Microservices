package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.dto.PriceDto;
import org.example.userservice.dto.ProductLongDto;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.exception.ProductNotFoundException;
import org.example.userservice.mapper.CartItemMapper;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.Price;
import org.example.userservice.model.product.ProductLong;
import org.example.userservice.model.product.ProductShort;
import org.example.userservice.repository.cart.CartRepository;
import org.example.userservice.repository.product.ProductLongRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID cartId = UUID.randomUUID();

    @Mock
    private CartItemMapper mapper;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductLongRepository productLongRepository;

    @InjectMocks
    private CartServiceImpl service;

    private CartItemRequest request;
    private CartItemResponse response;
    private ProductLong product;

    @BeforeEach
    void setUp() {
        int quantity = 10;
        request = new CartItemRequest(1, quantity);

        ProductLongDto productDto = new ProductLongDto(
                1,
                "product",
                Collections.emptyList(),
                new PriceDto(10f, "USD"),
                Collections.emptyList(),
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f
        );

        response = new CartItemResponse(productDto, quantity);

        ProductShort productShort = ProductShort.builder()
                .id(1)
                .name("product")
                .price(Price.builder()
                        .id(1)
                        .amount(10f)
                        .currency("USD")
                        .build())
                .categories(Collections.emptyList())
                .images(Collections.emptyList())
                .build();

        product = ProductLong.builder()
                .id(1)
                .lengthInM(1.0f)
                .widthInM(1.0f)
                .heightInM(1.0f)
                .netWeightInKg(1.0f)
                .grossWeightInKg(1.0f)
                .productShort(productShort)
                .build();

        productShort.setProductLong(product);
    }

    @Test
    void addItemToCart_ShouldThrowProductNotFoundException_WhenProductNotFoundAndNotAuthenticated() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.empty());

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addItemToCart(null, request, cartId, httpResponse)
        );
    }

    @Test
    void addItemToCart_ShouldThrowProductNotFoundException_WhenProductNotFoundAndAuthenticated() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.empty());

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addItemToCart(auth, request, cartId, httpResponse)
        );
    }

    @Test
    void addItemToCart_ShouldAndReturnCreatedItem_WhenNotAuthenticatedAndCartIdNotSpecified() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Cart stubCartId = Cart.builder()
                .id(cartId)
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(stubCartId);
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        CartItemResponse result = service.addItemToCart(null, request, null, httpResponse);

        verify(httpResponse, times(1)).addCookie(any(Cookie.class));
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieException_WhenCartNotFoundByCookieAndNotAuthenticated() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(null, request, cartId, httpResponse)
        );
    }

    @Test
    void addItemToCart_ShouldAddCookieAndReturnCreatedItem_WhenNotAuthenticatedAndCartIdSpecified() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        CartItemResponse result = service.addItemToCart(null, request, cartId, httpResponse);

        assertEquals(1, stubbedCart.getItems().size());
        verify(httpResponse, times(1)).addCookie(any(Cookie.class));
        verify(cartRepository, times(1)).save(stubbedCart);
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldCreateNewCartAndReturnCreatedCartItem_WhenAuthenticatedAndNoCartAssignedAndNoCookieSpecified() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);

        CartItemResponse result = service.addItemToCart(auth, request, null, httpResponse);

        verify(cartRepository, times(1)).save(any(Cart.class));
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldAddItemToCartAndReturnAddedItem_WhenAuthenticatedAndCartIsAssignedAndNoCookieSpecified() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);

        CartItemResponse result = service.addItemToCart(auth, request, null, httpResponse);

        verify(cartRepository, times(1)).save(stubbedCart);
        assertEquals(1, stubbedCart.getItems().size());
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieException_WhenCartIsAlreadyAssignedAndCookieIdSpecified() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(auth, request, cartId, httpResponse)
        );

        verify(cartRepository, never()).save(stubbedCart);
    }

    @Test
    void addItemToCart_ShouldAssignCartAndDeleteCookieAndAddItemToCart_WhenAuthenticatedAndCookieSpecified() {

        when(productLongRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(cartRepository.findByUserId(userId)).thenAnswer(invocationOnMock -> {
            UUID requestedCartUserId = invocationOnMock.getArgument(0);
            if (stubbedCart.getUserId().equals(requestedCartUserId)) {
                return Optional.of(stubbedCart);
            }

            Assertions.fail("Cart is not assigned to user correctly");
            throw new IllegalStateException("This line should never be reached");
        });

        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);

        CartItemResponse result = service.addItemToCart(auth, request, cartId, httpResponse);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponse, times(1)).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();
        assertNotNull(passedCookie);
        assertEquals(0, passedCookie.getMaxAge());

        verify(cartRepository, times(2)).save(stubbedCart);
        assertEquals(1, stubbedCart.getItems().size());

        assertEquals(response, result);
    }

    private Authentication mockAuthentication() {

        Authentication mockAuth = mock(Authentication.class);
        Jwt principal = mock(Jwt.class);

        when(mockAuth.getPrincipal()).thenReturn(principal);
        String userIdClaimName = "sub";
        when(principal.getClaimAsString(userIdClaimName)).thenReturn(userId.toString());
        return mockAuth;
    }
}