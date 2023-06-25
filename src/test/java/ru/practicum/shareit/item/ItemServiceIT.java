package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIT {

    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    BookingService bookingService;
    UserDto userDto;
    UserDto owner;
    ItemDto itemDto;
    ItemRequestDto itemRequestDto;

    CommentDto commentDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");
        userDto = userService.createUser(userDto);
        owner = new UserDto(
                2L,
                "AlexanderOwner",
                "testOwner@mail.ru");
        owner = userService.createUser(owner);
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                null,
                null,
                List.of(new CommentDto()));
        itemDto = itemService.createItem(owner.getId(), itemDto);
        itemRequestDto = new ItemRequestDto(
                1L,
                "description",
                userDto,
                created,
                List.of(itemDto));
        itemRequestDto = itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);
    }

    @Test
    void createItem_whenUserFound_thenReturnedItem() {
        assertThat(itemService.getItem(1L, 1L).getName(), equalTo(itemDto.getName()));
        assertThat(itemService.getItem(1L, 1L).getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void createItem_whenUserNotFound_thenReturnNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemService.createItem(0L, itemDto));
    }

    @Test
    void updateItem_whenUserAndItemDtoFound_thenReturnedUpdatedItem() {
        itemDto.setName("nameUpdate");
        itemDto.setDescription("descriptionUpdate");

        ItemDto actual = itemService.updateItem(owner.getId(), itemDto.getId(), itemDto);
        assertThat(actual.getName(), equalTo("nameUpdate"));
        assertThat(actual.getDescription(), equalTo("descriptionUpdate"));
    }

    @Test
    void updateItem_whenUserNotFound_thenReturnedNotFoundException() {
        Long wrongOwnerId = 10000L;
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(wrongOwnerId, itemDto.getId(), itemDto));
    }

    @Test
    void updateItem_whenItemDtoNotFound_thenReturnedNotFoundException() {
        Long wrongItemId = itemDto.getId() + 10000;
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(owner.getId(), wrongItemId, itemDto));
    }

    @Test
    void findAllItems_whenUserFound_thenReturnedItems() {
        List<ItemDto> actual = (List<ItemDto>) itemService.findAllItems(owner.getId());
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.get(0).getName(), equalTo(itemDto.getName()));
    }

    @Test
    void getItem_whenUserAndItemDtoFound_thenReturnedItem() {
        assertThat(itemDto.getName(), equalTo(itemService.getItem(1L, 1L).getName()));
        assertThat(itemDto.getDescription(), equalTo(itemService.getItem(1L, 1L).getDescription()));

    }

    @Test
    void getItem_whenUserEqualOwner_thenReturnedItem() {
        assertThat(itemDto.getName(), equalTo(itemService.getItem(owner.getId(), itemDto.getId()).getName()));
        assertThat(itemDto.getDescription(), equalTo(itemService.getItem(owner.getId(), itemDto.getId()).getDescription()));

    }

    @Test
    void getItem_whenItemDtoNotFound_thenReturnedNotFoundException() {
        Long wrongItemId = itemDto.getId() + 10000;
        assertThrows(NotFoundException.class,
                () -> itemService.getItem(1L, wrongItemId));
    }

    @Test
    void getItem_whenUserNotFound_thenReturnedNotFoundException() {
        Long wrongUserId = 10000L;
        assertThrows(NotFoundException.class,
                () -> itemService.getItem(wrongUserId, 1L));
    }

    @Test
    void getItemById() {
        assertThat(itemDto.getName(), equalTo(itemService.getItemById(1L).getName()));
    }

    @Test
    void getItemById_whenItemDtoNotFound_thenReturnedNotFoundException() {
        Long wrongItemId = itemDto.getId() + 10000;
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(wrongItemId));
    }
}