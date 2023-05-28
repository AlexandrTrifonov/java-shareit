package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    Item getItemById(Long id);

    Collection<Item> findAllItems(Long userId);

    Collection<Item> search(String text);
}
