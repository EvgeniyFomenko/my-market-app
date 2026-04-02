package ru.practicum.mymarketapp.entity.dto;

import ru.practicum.mymarketapp.entity.Item;

import java.util.Objects;

public class ItemDtoConverter {
    public static ItemDto toDto (Item item) {
        if (Objects.isNull(item)){
            return new ItemDto();
        }
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setTitle(item.getTitle());
        itemDto.setDescription(item.getDescription());
        itemDto.setImgPath(item.getImgPath());
        itemDto.setCount(item.getCount());
        itemDto.setPrice(item.getPrice());
        return itemDto;
    }

    public static Item fromDto (ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setImgPath(itemDto.getImgPath());
        item.setCount(itemDto.getCount());
        item.setPrice(itemDto.getPrice());
        return item;
    }
}
