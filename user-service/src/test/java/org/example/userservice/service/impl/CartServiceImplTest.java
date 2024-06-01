package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.dto.PriceDto;
import org.example.userservice.dto.ProductLongDto;
import org.example.userservice.exception.CartItemNotFoundException;
import org.example.userservice.exception.CartNotFoundException;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.exception.ProductNotFoundException;
import org.example.userservice.mapper.CartItemMapper;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.Price;
import org.example.userservice.model.product.ProductLong;
import org.example.userservice.model.product.ProductShort;
import org.example.userservice.repository.cart.CartItemRepository;
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
    private CartRepository cartRepo;

    @Mock
    private CartItemRepository cartItemRepo;

    @Mock
    private ProductLongRepository productLongRepo;

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
    void addItemToCart_ShouldThrowProductNotFoundExc_WhenProductNotFoundAndNotAuthenticated() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.empty());

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addItemToCart(null, request, cartId, httpResponse)
        );
    }

    @Test
    void addItemToCart_ShouldThrowProductNotFoundExc_WhenProductNotFoundAndAuthenticated() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.empty());

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                ProductNotFoundException.class,
                () -> service.addItemToCart(auth, request, cartId, httpResponse)
        );
    }

    @Test
    void addItemToCart_ShouldAndReturnCreatedItem_WhenNotAuthenticatedAndCartIdNotSpecified() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));

        Cart stubCartId = Cart.builder()
                .id(cartId)
                .build();

        when(cartRepo.save(any(Cart.class))).thenReturn(stubCartId);
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        CartItemResponse result = service.addItemToCart(null, request, null, httpResponse);

        verify(httpResponse, times(1)).addCookie(any(Cookie.class));
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieExc_WhenCartNotFoundByCookieAndNotAuthenticated() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));
        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(null, request, cartId, httpResponse)
        );
    }

    @Test
    void addItemToCart_ShouldAddCookieAndReturnCreatedItem_WhenNotAuthenticatedAndCartIdSpecified() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        CartItemResponse result = service.addItemToCart(null, request, cartId, httpResponse);

        assertEquals(1, stubbedCart.getItems().size());
        verify(httpResponse, times(1)).addCookie(any(Cookie.class));
        verify(cartRepo, times(1)).save(stubbedCart);
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldCreateNewCartAndReturnCreatedCartItem_WhenAuthenticatedAndNoCartAssignedAndNoCookieSpecified() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);

        CartItemResponse result = service.addItemToCart(auth, request, null, httpResponse);

        verify(cartRepo, times(1)).save(any(Cart.class));
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldAddItemToCartAndReturnAddedItem_WhenAuthenticatedAndCartIsAssignedAndNoCookieSpecified() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));
        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);

        CartItemResponse result = service.addItemToCart(auth, request, null, httpResponse);

        verify(cartRepo, times(1)).save(stubbedCart);
        assertEquals(1, stubbedCart.getItems().size());
        assertEquals(response, result);
    }

    @Test
    void addItemToCart_ShouldThrowInvalidCartIdCookieExc_WhenCartIsAlreadyAssignedAndCookieIdSpecified() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.addItemToCart(auth, request, cartId, httpResponse)
        );

        verify(cartRepo, never()).save(stubbedCart);
    }

    @Test
    void addItemToCart_ShouldAssignCartAndDeleteCookieAndAddItemToCart_WhenAuthenticatedAndCookieSpecified() {

        when(productLongRepo.findById(request.productId())).thenReturn(Optional.of(product));

        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

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

        when(mapper.toResponse(any(CartItem.class))).thenReturn(response);

        CartItemResponse result = service.addItemToCart(auth, request, cartId, httpResponse);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponse, times(1)).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();
        assertNotNull(passedCookie);
        assertEquals(0, passedCookie.getMaxAge());

        verify(cartRepo, times(2)).save(stubbedCart);
        assertEquals(1, stubbedCart.getItems().size());

        assertEquals(response, result);
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdCookieExc_WhenNotAuthenticatedAndCookieIdNull() {

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(null, request, null, httpResponse)
        );
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdCookieExc_WhenNotAuthenticatedCartNotFoundByIdFromCookie() {

        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(null, request, cartId, httpResponse)
        );
    }

    @Test
    void updateItemQuantity_ShouldThrowCartNotFoundExc_WhenAuthenticatedAndCookieIsNull() {

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());
        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                CartNotFoundException.class,
                () -> service.updateItemQuantity(auth, request, null, httpResponse)
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
        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(auth, request, cartId, httpResponse)
        );

        verify(cartRepo, never()).save(any(Cart.class));
        verify(httpResponse, never()).addCookie(any(Cookie.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowInvalidCartIdExc_WhenAuthenticatedAndFoundCartAlreadyAssigned() {

        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());
        Authentication auth = mockAuthentication();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                InvalidCartIdCookieException.class,
                () -> service.updateItemQuantity(auth, request, cartId, httpResponse)
        );

        verify(cartRepo, never()).save(any(Cart.class));
        verify(httpResponse, never()).addCookie(any(Cookie.class));
    }

    @Test
    void updateItemQuantity_ShouldThrowCartItemNotFoundExc_WhenItemNotFoundInCart() {

        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(null)
                .items(new ArrayList<>())
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        assertThrows(
                CartItemNotFoundException.class,
                () -> service.updateItemQuantity(null, request, cartId, httpResponse)
        );

        verify(cartItemRepo, never()).save(any(CartItem.class));
    }

    @Test
    void updateItemQuantity_ShouldReturnUpdatedItem_WhenNotAuthenticated() {

        CartItem itemToBeUpdated = new CartItem(1, product, 9);
        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .items(Collections.singletonList(itemToBeUpdated))
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        when(mapper.toResponse(itemToBeUpdated)).thenReturn(response);

        CartItemResponse result = service.updateItemQuantity(null, request, cartId, httpResponse);

        assertEquals(10, result.quantity());
        assertEquals(10, itemToBeUpdated.getQuantity());
        assertEquals(response, result);
    }

    @Test
    void updateItemQuantity_ShouldReturnUpdatedItemAndDeleteCookie_WhenAuthenticatedAndCookieSpecified() {

        CartItem itemToBeUpdated = new CartItem(1, product, 9);
        Cart stubbedCart = Cart.builder()
                .id(cartId)
                .userId(null)
                .items(Collections.singletonList(itemToBeUpdated))
                .build();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(stubbedCart));
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(stubbedCart));
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Authentication auth = mockAuthentication();
        when(mapper.toResponse(itemToBeUpdated)).thenReturn(response);

        CartItemResponse result = service.updateItemQuantity(auth, request, cartId, httpResponse);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(httpResponse, times(1)).addCookie(cookieCaptor.capture());

        Cookie passedCookie = cookieCaptor.getValue();
        assertNotNull(passedCookie);
        assertEquals(0, passedCookie.getMaxAge());

        assertEquals(10, result.quantity());
        assertEquals(10, itemToBeUpdated.getQuantity());
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