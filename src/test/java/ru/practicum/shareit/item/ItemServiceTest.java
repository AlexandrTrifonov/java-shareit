package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    UserDto userDto;
    User user;
    ItemDto itemDto;
    Item item;
    Booking nextBooking;
    Booking lastBooking;
    CommentDto commentDto;
    Comment comment;
    final LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    final LocalDateTime startLast = currentTime.minusHours(2);
    final LocalDateTime endLast = currentTime.minusHours(1);
    final LocalDateTime startNext = currentTime.plusHours(3);
    final LocalDateTime endNext = currentTime.plusDays(1);


    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");
        user = new User(
                1L,
                "Alexander",
                "test@mail.ru");
        itemDto = new ItemDto(
                1L,
                "Предмет",
                "Описание предмета",
                true,
                888L,
                null,
                null,
                null);
        item = new Item(
                1L,
                "Предмет",
                "Описание предмета",
                true,
                888L,
                user);
        nextBooking = new Booking(
                1L,
                startNext,
                endNext,
                item,
                user,
                "APPROVED");
        lastBooking = new Booking(
                1L,
                startLast,
                endLast,
                item,
                user,
                "APPROVED");
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        commentDto = new CommentDto(
                1L,
                "commentText",
                "Alexander",
                created);
        comment = new Comment(
                1L,
                "commentText",
                item,
                user,
                created);
    }

    @Test
    void createItem_whenItemDtoIsValid_thenReturnedUserDto() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(user.getId(), itemDto);

        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName());
    }

    @Test
    void createItem_whenUserIdOrUserIsNotValid_thenReturnNotFoundException() {
        Long userIdNull = null;

        Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(userIdNull, itemDto));

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(userDto.getId(), itemDto));
    }

    @Test
    void updateItem_whenItemDtoIsValid_thenReturnedUserDto() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);

        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        ItemDto actualItemDto = itemService.updateItem(user.getId(), itemDto.getId(), itemDto);

        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName());
    }

    @Test
    void updateItem_whenItemDtoIsValidAndNotNull_thenReturnedUserDto() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.updateItem(user.getId(), itemDto.getId(), itemDto);

        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName());
    }

    @Test
    void updateItem_whenUserIdIsNull_thenReturnNotFoundException() {
        Long userIdNull = null;

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userIdNull, itemDto.getId(), itemDto));
    }

    @Test
    void updateItem_whenItemIsNotFound_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user.getId(), itemDto.getId(), itemDto));
    }

    @Test
    void updateItem_whenUserIsNotFound_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user.getId(), itemDto.getId(), itemDto));
    }

    @Test
    void findAllItems() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByOwnerOrderById(any())).thenReturn(List.of(item));
        when(bookingRepository.getNextBooking(any(), any(), any())).thenReturn(Optional.ofNullable(nextBooking));
        when(bookingRepository.getLastBooking(any(), any(), any())).thenReturn(Optional.ofNullable(lastBooking));
        when(commentRepository.getCommentsForItem(any())).thenReturn(List.of(comment));

        List<ItemDto> actualListItemDto = itemService.findAllItems(user.getId());

        Assertions.assertEquals(itemDto.getName(), actualListItemDto.get(0).getName());
    }

    @Test
    void getItem() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.getNextBooking(any(), any(), any())).thenReturn(Optional.ofNullable(nextBooking));
        when(bookingRepository.getLastBooking(any(), any(), any())).thenReturn(Optional.ofNullable(lastBooking));
        when(commentRepository.getCommentsForItem(any())).thenReturn(List.of(comment));

        ItemDto actual = itemService.getItem(user.getId(), item.getId());

        Assertions.assertEquals(itemDto.getName(), actual.getName());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        ItemDto actual = itemService.getItemById(item.getId());

        Assertions.assertEquals(itemDto.getName(), actual.getName());
    }

    @Test
    void getItemById_whenItemIdWrong_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(itemDto.getId()));
    }

    @Test
    void search() {
        String text = "Опис";
        when(itemRepository.search(any())).thenReturn(List.of(item));

        List<ItemDto> actual = (List<ItemDto>) itemService.search(text);

        Assertions.assertEquals(itemDto.getName(), actual.get(0).getName());
    }

    @Test
    void search_whenTextIsEmpty_thenReturnNewArrayList() {
        String text = "";

        List<ItemDto> actual = (List<ItemDto>) itemService.search(text);

        Assertions.assertEquals(actual.size(), 0);
    }

    @Test
    void createComment() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.checkStatusBooking(any(), any(), any())).thenReturn("APPROVED");
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actual = itemService.createComment(user.getId(), item.getId(), commentDto);

        Assertions.assertEquals(commentDto.getText(), actual.getText());
    }
}