package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    BookingDto bookingDto;
    Booking booking;
    UserDto userDto;
    User user;
    User notOwner;
    UserDto notOwnerDto;
    ItemDto itemDto;
    Item item;
    Booking nextBooking;
    Booking lastBooking;
    CommentDto commentDto;
    Comment comment;
    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;
    final LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    final LocalDateTime start = currentTime.plusHours(1);
    final LocalDateTime end = currentTime.plusHours(2);
    final LocalDateTime startLast = currentTime.minusHours(2);
    final LocalDateTime endLast = currentTime.minusHours(1);
    final LocalDateTime startNext = currentTime.plusHours(3);
    final LocalDateTime endNext = currentTime.plusDays(1);


    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "Alexander",
                "test@mail.ru");
        item = new Item(
                1L,
                "Предмет",
                "Описание предмета",
                true,
                888L,
                user);
        bookingDto = new BookingDto(
                1L,
                start,
                end,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        booking = new Booking(
                1L,
                start,
                end,
                item,
                user,
                "APPROVED");
        userDto = new UserDto(
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
        notOwner = new User(
                2L,
                "Alexander2",
                "test2@mail.ru");
        notOwnerDto = new UserDto(
                2L,
                "Alexander2",
                "test2@mail.ru");
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
        itemRequestDto = new ItemRequestDto(
                1L,
                "Описание реквеста",
                notOwnerDto,
                created,
                null);
        itemRequest = new ItemRequest(
                1L,
                "Описание реквеста",
                notOwner,
                created,
                null);
    }

    @Test
    void findItemRequestsOwner() {
        List items = new ArrayList();
        items.add(item);
        itemRequest.setItems(items);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findByUser(any())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> actual = itemRequestService.findItemRequestsOwner(user.getId());

        Assertions.assertEquals(itemRequestDto.getDescription(), actual.get(0).getDescription());
    }

    @Test
    void createItemRequest() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto actual = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        Assertions.assertEquals(itemRequestDto.getDescription(), actual.getDescription());
    }

    @Test
    void createItemRequest_whenUserIsNotFound_thenNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(user.getId(), itemRequestDto));
    }

    @Test
    void getItemRequestById() {
        List items = new ArrayList();
        items.add(item);
        itemRequest.setItems(items);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDto actual = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        Assertions.assertEquals(itemRequestDto.getDescription(), actual.getDescription());
    }

    @Test
    void getItemRequestById_whenItemRequestIsNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()));
    }
}