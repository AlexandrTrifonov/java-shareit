package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIT {
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    UserService userService;
    ItemRequestDto itemRequestDto;
    UserDto userDto;
    UserDto user2Dto;
    ItemDto itemDto;
    CommentDto commentDto;
    LocalDateTime created;
    ItemRequestDto result;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        userDto = new UserDto(1L, "Alexander", "test@mail.ru");
        user2Dto = new UserDto(2L, "Alexander2", "test2@mail.ru");
        itemRequestDto = new ItemRequestDto(1L, "description", userDto, created, List.of(new ItemDto()));
        userService.createUser(userDto);
        userService.createUser(user2Dto);
    }

    @Test
    void createItemRequest() {
        result = itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);

        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void createItemRequest_whenUserNotFound_thenReturnedNotFoundException() {
        Long wrongUserId = 10000L;

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(wrongUserId, itemRequestDto));
    }

    @Test
    void getItemRequestById() {
        itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);

        assertThat(itemRequestService.getItemRequestById(userDto.getId(),
                itemRequestDto.getId()).getId(), equalTo(itemRequestDto.getId()));
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenReturnedNotFoundException() {
        Long wrongRequestId = itemRequestDto.getId() + 10000;

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(userDto.getId(), wrongRequestId));
    }

    @Test
    void findAllItemRequests() {
        List<ItemRequestDto> result = itemRequestService.findAllItemRequests(user2Dto.getId(), 0, 10);

        assertTrue(result.isEmpty());
    }
}