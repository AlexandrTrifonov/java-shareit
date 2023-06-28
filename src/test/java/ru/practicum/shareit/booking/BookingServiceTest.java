package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
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
class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    BookingDto bookingDto;
    Booking booking;
    UserDto userDto;
    User user;
    User notOwner;
    ItemDto itemDto;
    Item item;
    Booking nextBooking;
    Booking lastBooking;
    CommentDto commentDto;
    Comment comment;
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
    void createBooking_whenBookingDtoIsValid_thenReturnedBookerDto() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto actual = bookingService.createBooking(notOwner.getId(), bookingDto);

        Assertions.assertEquals(bookingDto.getItemId(), actual.getItemId());
    }

    @Test
    void createBooking_whenItemIsNotFound_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(notOwner.getId(), bookingDto));
    }

    @Test
    void createBooking_whenItemAvailableIsFalse_thenReturnBadRequest() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        item.setAvailable(false);

        Assertions.assertThrows(BadRequest.class,
                () -> bookingService.createBooking(notOwner.getId(), bookingDto));
    }

    @Test
    void createBooking_whenUserIsOwner_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    void createBooking_whenStartIsEnd_thenReturnBadRequest() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        bookingDto.setEnd(start);

        Assertions.assertThrows(BadRequest.class,
                () -> bookingService.createBooking(notOwner.getId(), bookingDto));
    }

    @Test
    void createBooking_whenStartIsAfterEnd_thenReturnBadRequest() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        bookingDto.setEnd(endLast);

        Assertions.assertThrows(BadRequest.class,
                () -> bookingService.createBooking(notOwner.getId(), bookingDto));
    }

    @Test
    void createBooking_whenUserIsNotFound_thenReturnNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(notOwner.getId(), bookingDto));
    }

    @Test
    void approveBooking_whenApprovedIsTrue_thenReturnBookingWithStatusApproved() {
        booking.setStatus("WAITING");
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto actual = bookingService.approveBooking(user.getId(), bookingDto.getId(), true);

        Assertions.assertEquals(bookingDto.getItemId(), actual.getItemId());
        Assertions.assertEquals("APPROVED", actual.getStatus());
    }

    @Test
    void approveBooking_whenApprovedIsFalse_thenReturnBookingWithStatusRejected() {
        booking.setStatus("WAITING");
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto actual = bookingService.approveBooking(user.getId(), bookingDto.getId(), false);

        Assertions.assertEquals(bookingDto.getItemId(), actual.getItemId());
        Assertions.assertEquals("REJECTED", actual.getStatus());
    }

    @Test
    void approveBooking_whenUserIsNotOwner_thenReturnNotFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(notOwner.getId(), bookingDto.getId(), true));
    }

    @Test
    void approveBooking_whenBookingIsEmpty_thenReturnNotFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(user.getId(), bookingDto.getId(), true));
    }

    @Test
    void approveBooking_whenBookingIsAPPROVED_thenReturnBadRequest() {
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        Assertions.assertThrows(BadRequest.class,
                () -> bookingService.approveBooking(user.getId(), bookingDto.getId(), true));
    }

    @Test
    void getBookingById_whenUserIsCorrect_thenReturnBookingDto() {
        booking.setStatus("WAITING");
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        BookingDto actual = bookingService.getBookingById(user.getId(), bookingDto.getId());

        Assertions.assertEquals(bookingDto.getItemId(), actual.getItemId());
    }

    @Test
    void getBookingById_whenUserIsNotOwner_thenReturnNotFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(2L, bookingDto.getId()));
    }

    @Test
    void getBookingById_whenBookingIsNotFound_thenReturnNotFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(user.getId(), bookingDto.getId()));
    }

    @Test
    void findAllBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "ALL", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), actual.get(0).getItemId());
    }

    @Test
    void findAllBookings_whenStatusIsUnknownState_thenReturnUnsupportedStatus() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        //    List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "Unknown state", 0, 10);

        Assertions.assertThrows(UnsupportedStatus.class,
                () -> bookingService.findAllBookings(user.getId(), "Unknown state", 0, 10));
    }

    @Test
    void findAllBookings_whenStatusIsPast_thenReturnBookingCurrentStatus() {
        booking.setStatus("CURRENT");
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "CURRENT", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), 1L);
    }

    @Test
    void findAllBookings_whenStatusIsPast_thenReturnBookingPastStatus() {
        booking.setStatus("PAST");
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "PAST", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), 1L);
    }

    @Test
    void findAllBookings_whenStatusIsPast_thenReturnBookingFutureStatus() {
        booking.setStatus("FUTURE");
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "FUTURE", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), 1L);
    }

    @Test
    void findAllBookings_whenStatusIsPast_thenReturnBookingWaitingStatus() {
        booking.setStatus("WAITING");
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "WAITING", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), 1L);
    }

    @Test
    void findAllBookings_whenStatusIsPast_thenReturnBookingRejectedStatus() {
        booking.setStatus("REJECTED");
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookings(user.getId(), "REJECTED", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), 1L);
    }

    @Test
    void findAllBookings_whenUserNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookings(user.getId(), "ALL", 0, 10));
    }

    @Test
    void findAllBookingOwner() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByOwner(any())).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actual = bookingService.findAllBookingsOwner(user.getId(), "ALL", 0, 10);

        Assertions.assertEquals(bookingDto.getItemId(), actual.get(0).getItemId());
    }

    @Test
    void findAllBookings_whenOwnerNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingsOwner(user.getId(), "ALL", 0, 10));
    }

}