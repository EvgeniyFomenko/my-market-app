package ru.practicum.mymarketapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="cart_user")
public class CartUser {

    @Id
    private Long id;
    private Long cartId;
    private Long userId;

    public CartUser() {
    }

    public CartUser(Long id, Long cartId, Long userId) {
        this.id = id;
        this.cartId = cartId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
