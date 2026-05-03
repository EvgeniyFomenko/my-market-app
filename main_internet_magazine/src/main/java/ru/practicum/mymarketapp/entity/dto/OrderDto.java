package ru.practicum.mymarketapp.entity.dto;

import java.util.List;

public class OrderDto {
    private Long id;
    private List<ItemDto> items;
    private Long totalSum;

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemDto> items() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        this.items = items;
    }

    public Long totalSum() {
        return totalSum;
    }

    public void setTotalSum(Long totalSum) {
        this.totalSum = totalSum;
    }
}
