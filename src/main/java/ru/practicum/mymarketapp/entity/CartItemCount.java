package ru.practicum.mymarketapp.entity;

import jakarta.persistence.*;


@Entity
@Table
public class CartItemCount {//Количество вещей в карзине
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "generated_counter")
    @SequenceGenerator(allocationSize = 1, name = "generated_counter")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order orderId;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item itemId;
    //Количество товаров в корзине/заказе
    private int quantity;

    public Order getOrderId() {
        return orderId;
    }

    public void setOrderId(Order orderId) {
        this.orderId = orderId;
    }

    public Item getItemId() {
        return itemId;
    }

    public void setItemId(Item itemId) {
        this.itemId = itemId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
