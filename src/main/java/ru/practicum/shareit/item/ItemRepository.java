package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(Optional<User> owner);

    List<Item> findByOwnerOrderById(Optional<User> owner);

    @Query(nativeQuery = true, value = "select * from items i where i.available = true and " +
            "(upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    Collection<Item> search(String text);

    List<Item> findByRequestId(Long requestId);
}
