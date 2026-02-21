package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import ru.practicum.mymarketapp.entity.CartItemCount;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public Order findNewOrder() {
        List<Order> orders = orderRepository.findByIsPaidFalse();
        Order order = orders.stream().filter(o -> !o.isPaid()).findFirst().orElse(null);
        if (Objects.isNull(order)) { // Если карзина пустая то создаем новую карзину
            order = new Order();
            order.setPaid(false);
            order.setTotal(new BigDecimal(0));
            order = addOrder(order);
        }

        return order;
    }

    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    public void delete(Order order) {
        orderRepository.delete(order);
    }

    public List<Order> findPaidOrdersIsPaidTrue() {
        return orderRepository.findByIsPaidTrue();
    }

    public void updatePaid(Order order) {
        order.setPaid(true);
        orderRepository.save(order);
    }

    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public void changePriceOrderByActionOnCartItemCount(Order order, String action, CartItemCount cartItemCount){
        BigDecimal total = null; //Обновляем стоимость заказа
        if ("PLUS".equals(action)) {
            total = order.getTotal().add(new BigDecimal(cartItemCount.getItemId().getPrice()));
        } else if ("MINUS".equals(action)) {
            total = order.getTotal().subtract(new BigDecimal(cartItemCount.getItemId().getPrice()));
            if (total.compareTo(new BigDecimal(0)) == 0 ){
                if (Objects.nonNull(order.getId())) {
                    delete(order);
                }
                return;
            }
        } else  if ("DELETE".equals(action)) {
             total = order.getTotal();
            for (int i = 0; i < cartItemCount.getQuantity(); i++) {
                total = total.subtract(new BigDecimal(cartItemCount.getItemId().getPrice()));
            }
            if (total.compareTo(new BigDecimal(0)) == 0) {
                delete(order);
                return;
            }
        }
        order.setTotal(total);
        orderRepository.save(order);
    }
}
