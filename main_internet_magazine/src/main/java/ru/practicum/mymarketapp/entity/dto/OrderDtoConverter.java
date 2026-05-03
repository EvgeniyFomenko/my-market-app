package ru.practicum.mymarketapp.entity.dto;

import ru.practicum.mymarketapp.entity.Order;

import java.util.List;

public class OrderDtoConverter {

    public static OrderDto toDto(Order order, List<ItemDto> itemDtos){
        OrderDto orderDto = new OrderDto();
        orderDto.setItems(itemDtos);
        orderDto.setId(order.getId());
        orderDto.setTotalSum(order.getTotal().longValue());
        return orderDto;
    }
}
