package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap<Long, Item> items = new HashMap<>();
    private HashMap<Long, List<Item>> itemsUser = new HashMap<>();
    private Long idItem = 1L;

    @Override
    public Item createItem(Long userId, Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        if (!itemsUser.containsKey(userId)) {
            itemsUser.put(userId, new ArrayList<>());
        }
        List<Item> itemsByUser = itemsUser.get(userId);
        itemsByUser.add(item);
        itemsUser.put(userId, itemsByUser);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        items.put(item.getId(), item);
        List<Item> itemsByUser = itemsUser.get(userId);
        itemsByUser.add(item);
        for (Item itemToDelete : itemsByUser) {
            if (itemToDelete.getId() == item.getId()) itemsByUser.remove(itemToDelete);
        }
        itemsUser.put(userId, itemsByUser);
        return item;
    }

    @Override
    public Item getItemById(Long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> findAllItems(Long userId) {
        return itemsUser.get(userId);
    }

    @Override
    public Collection<Item> search(String text) {
        List<Item> search = new ArrayList<>();
        if (text.isBlank()) return search;
        List<Item> searchName = items.values().stream()
                .filter(x -> x.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(x -> x.getAvailable().equals(true))
                .collect(Collectors.toList());
        List<Item> searchDescription = items.values().stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(x -> x.getAvailable().equals(true))
                .collect(Collectors.toList());
        search.addAll(searchName);
        search.addAll(searchDescription);
        search = search.stream().distinct().collect(Collectors.toList());
        return search;
    }

    private Long generateItemId() {
        return idItem++;
    }

    public List<Item> itemsByUser(Long userId) {
        return itemsUser.get(userId);
    }
}
