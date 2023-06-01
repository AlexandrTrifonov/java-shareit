package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    Collection<ItemDto> findAllItems(Long userId);

    ItemDto getItemById(Long id);

    Collection<ItemDto> search(String text);
}
