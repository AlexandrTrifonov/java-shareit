package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    ItemDto itemDto;
    ItemDto item2Dto;
    Item item;
    Item item2;
    User user;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "nameUser",
                "email@mail.ru");
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                null,
                null,
                null);
        item2Dto = new ItemDto(
                2L,
                "name2",
                "description2",
                true,
                1L,
                null,
                null,
                null);
        item = ItemMapper.toItem(user, itemDto);
        item2 = ItemMapper.toItem(user, item2Dto);
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
    }

    @Test
    void findByOwner() {
        List<Item> itemList = itemRepository.findByOwner(Optional.ofNullable(user));

        assertFalse(itemList.isEmpty());
        assertEquals(2, itemList.size());
        assertEquals("name", itemList.get(0).getName());
    }

    @Test
    void findByOwnerOrderById() {
        List<Item> itemList = itemRepository.findByOwnerOrderById(Optional.ofNullable(user));

        assertFalse(itemList.isEmpty());
        assertEquals(2, itemList.size());
        assertEquals("name2", itemList.get(1).getName());
    }

    @Test
    void search() {
        List<Item> search = (List<Item>) itemRepository.search("e");

        assertFalse(search.isEmpty());
        assertEquals(2, search.size());

        List<Item> search2 = (List<Item>) itemRepository.search("e2");

        assertEquals(1, search2.size());
        assertEquals("name2", search2.get(0).getName());
    }
}