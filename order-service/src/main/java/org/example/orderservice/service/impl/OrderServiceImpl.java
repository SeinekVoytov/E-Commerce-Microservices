package org.example.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.communication.ProductServiceCommunicator;
import org.example.orderservice.communication.UserServiceCommunicator;
import org.example.orderservice.dto.cart.CartContentResponse;
import org.example.orderservice.dto.order.OrderDetailsResponse;
import org.example.orderservice.dto.order.OrderRequest;
import org.example.orderservice.dto.order.OrderResponse;
import org.example.orderservice.exception.CartIsEmptyException;
import org.example.orderservice.exception.InvalidQueryParameterException;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.exception.PickUpPointNotFoundException;
import org.example.orderservice.mapper.OrderDetailsMapper;
import org.example.orderservice.mapper.OrderItemMapper;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.model.Delivery;
import org.example.orderservice.model.DeliveryStatus;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderDetails;
import org.example.orderservice.model.OrderItem;
import org.example.orderservice.model.PickUpPoint;
import org.example.orderservice.repository.OrderDetailsRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.repository.PickUpPointRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Set<String> AVAILABLE_SORT_PARAMETERS = Set.of("createdAt");

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final PickUpPointRepository pickUpPointRepository;

    private final UserServiceCommunicator userServiceCommunicator;
    private final ProductServiceCommunicator productServiceCommunicator;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderDetailsMapper detailsMapper;

    @Override
    public Page<OrderResponse> getUserOrders(Jwt jwt,
                                             Pageable pageable) {

        validateSortParameters(pageable.getSort());
        UUID userId = retrieveUserIdFromJwt(jwt);
        return orderRepository.findAllByUserId(userId, pageable)
                .map(order -> orderMapper.toDto(
                        order,
                        productServiceCommunicator.getProductsByIds(
                                getAllProductIds(order.getItems())
                        ).products())
                );
    }

    @Override
    public OrderDetailsResponse getUserOrderDetailsById(Jwt jwt, int orderId) {
        UUID userId = retrieveUserIdFromJwt(jwt);

        return mapToResponse(
                orderDetailsRepository.findOrderLongByIdAndUserId(orderId, userId)
                        .orElseThrow(OrderNotFoundException::new)
        );
    }

    @Override
    public OrderDetailsResponse deleteUserOrderById(Jwt jwt, int orderId) {
        UUID userId = retrieveUserIdFromJwt(jwt);
        OrderDetails orderToBeDeleted = orderDetailsRepository.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);
        orderDetailsRepository.delete(orderToBeDeleted);
        return mapToResponse(orderToBeDeleted);
    }

    @Override
    public OrderDetailsResponse createOrder(Jwt jwt, OrderRequest request) {

        PickUpPoint pickUpPoint =
                pickUpPointRepository.findById(request.pickUpPointId()).orElseThrow(
                        () -> new PickUpPointNotFoundException(request.pickUpPointId())
                );

        CartContentResponse cartContent = userServiceCommunicator.getCartContent(jwt.getTokenValue());
        if (cartContent.items().isEmpty()) {
            throw new CartIsEmptyException();
        }

        communicator.clearCart(jwt.getTokenValue());

        OrderDetails orderDetails = OrderDetails.builder()
                .order(Order.builder()
                        .userId(retrieveUserIdFromJwt(jwt))
                        .delivery(Delivery.builder()
                                .pickUpPoint(pickUpPoint)
                                .status(DeliveryStatus.ORDER_RECEIVED)
                                .build()
                        )
                        .items(cartContent.items().stream()
                                .map(orderItemMapper::toEntityFromCartItem)
                                .collect(Collectors.toSet())
                        )
                        .build())
                .build();

        orderDetails = orderDetailsRepository.save(orderDetails);

        return detailsMapper.toDtoFromCartContentAndEntity(cartContent, orderDetails);
    }

    private OrderDetailsResponse mapToResponse(OrderDetails entity) {
        return detailsMapper.toDto(
                entity,
                productServiceCommunicator.getProductsByIds(
                        getAllProductIds(entity.getOrder().getItems())
                ).products()
        );
    }

    private List<Integer> getAllProductIds(Collection<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItem::getItemId).distinct().toList();
    }

    private void validateSortParameters(Sort sort) {
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!AVAILABLE_SORT_PARAMETERS.contains(property)) {
                throw new InvalidQueryParameterException("sort", property);
            }
        }
    }
}