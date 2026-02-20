package ru.practicum.mymarketapp.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartItemCountRepository;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartItemCountService {
    private final CartItemCountRepository cartItemCountRepository;

    public CartItemCountService(CartItemCountRepository cartItemCountRepository) {
        this.cartItemCountRepository = cartItemCountRepository;
    }

    public List<CartItemCount> findAll() {
        return cartItemCountRepository.findAll();
    }

    public void save(CartItemCount cartItemCount) {
        cartItemCountRepository.save(cartItemCount);
    }

    public CartItemCount findByItemIdAndOrderId(Item itemId, Order orderId) {
       return cartItemCountRepository.findByItemIdAndOrderId(itemId, orderId);
    }

    public void delete(CartItemCount cartItemCount) {
        cartItemCountRepository.delete(cartItemCount);
    }

    public List<Item> findItemByOrderId(Order order) {
        return cartItemCountRepository.findByOrderId(order).stream()
                .filter(e-> Objects.nonNull(e.getItemId()))
                .peek(e-> e.getItemId().setCount(e.getQuantity()))
                .map(CartItemCount::getItemId).toList();
    }

    public void changePriceCartByAction (CartItemCount cartItemCount, String action) {
        if (Objects.isNull(cartItemCount)) {
            return;
        }
        if("PLUS".equals(action) || "MINUS".equals(action)) {
            cartItemCount.setQuantity("PLUS".equals(action) ? cartItemCount.getQuantity() + 1 : cartItemCount.getQuantity() - 1);
            if (cartItemCount.getQuantity() == 0) {
                delete(cartItemCount);
            } else {
                save(cartItemCount);
            }
        } else if ("DELETE".equals(action)) {
            delete(cartItemCount);
        }

    }

    public List<CartItemCount> findCartItemCountByOrderId(Order order) {
        return cartItemCountRepository.findByOrderId(order);
    }

    public CartItemCount findByOrderAndItemId(Order order, Long id) {
        List<CartItemCount> cartItemCountUser = findCartItemCountByOrderId(order);
        Optional<CartItemCount> cartItemCountOptional = cartItemCountUser.stream()
                .filter(cartItemCount -> cartItemCount.getItemId().getId() == id).findAny();
        CartItemCount cartItemCount = cartItemCountOptional.orElse(null);
        return cartItemCount;
    }

    public CartItemCount createOrFindByOrderAndItemId(Order order,Item item) {
        CartItemCount cartItemCount = findByOrderAndItemId(order,item.getId());
        if (Objects.isNull(cartItemCount)) {
            cartItemCount = new CartItemCount();
            cartItemCount.setItemId(item);
            cartItemCount.setOrderId(order);
            save(cartItemCount);
        }
        return cartItemCount;
    }
}
