package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(Optional<Item> owner);
    @Query(nativeQuery = true, value = "select i from Item i where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))")
    Collection<Item> search(String text);

    /*    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    Item getItemById(Long id);

    Collection<Item> findAllItems(Long userId);

    Collection<Item> search(String text);*/
}
