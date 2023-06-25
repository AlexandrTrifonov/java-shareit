package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIT {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;
    UserDto userDto;
    UserDto booker;
    ItemDto itemDto;
    BookingDto bookingDto;
    BookingDto bookingDtoPast;
    CommentDto commentDto;
    BookingDto nextBookingDto;
    BookingDto lastBookingDto;
    final LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    final LocalDateTime start = currentTime.plusHours(1);
    final LocalDateTime end = currentTime.plusHours(2);
    final LocalDateTime startPast = currentTime.minusHours(3);
    final LocalDateTime endPast = currentTime.minusHours(2);
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
        booker = new UserDto(
                2L,
                "Alexander2",
                "test2@mail.ru");
        userDto = userService.createUser(userDto);
        booker = userService.createUser(booker);
        commentDto = new CommentDto(
                1L,
                "text",
                "authorName",
                currentTime);
        nextBookingDto = new BookingDto(
                1L,
                startNext,
                endNext,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        lastBookingDto = new BookingDto(
                1L,
                startLast,
                endLast,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                null,
                null,
                List.of(commentDto));
        itemService.createItem(userDto.getId(), itemDto);
        bookingDto = new BookingDto(
                1L,
                start,
                end,
                1L,
                itemDto,
                booker,
                2L,
                "WAITING");
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingDtoPast = new BookingDto(
                2L,
                startPast,
                endPast,
                1L,
                itemDto,
                booker,
                2L,
                "APPROVED");
        bookingService.createBooking(booker.getId(), bookingDtoPast);
    }

    @Test
    void createBooking() {
        BookingDto result = bookingService.getBookingById(2L, 1L);

        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getStart(), equalTo(bookingDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(result.getItemId(), equalTo(bookingDto.getItemId()));
        assertThat(result.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(result.getItem().getName(), equalTo(bookingDto.getItem().getName()));
        assertThat(result.getItem().getDescription(), equalTo(bookingDto.getItem().getDescription()));
        assertThat(result.getItem().getAvailable(), equalTo(bookingDto.getItem().getAvailable()));
        assertThat(result.getItem().getRequestId(), equalTo(bookingDto.getItem().getRequestId()));
        assertThat(result.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(result.getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(result.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void createBooking_whenItemIsNotFound_thenReturnedNotFoundException() {
        Long wrongItemId = 10000L;
        bookingDto.setItemId(wrongItemId);
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingDto));
    }

    @Test
    void createBooking_whenBookingIsNotValid_thenReturnedBadRequest() {
        LocalDateTime end = bookingDto.getStart().minusHours(2);
        bookingDto.setEnd(end);
        assertThrows(BadRequest.class,
                () -> bookingService.createBooking(2L, bookingDto));
    }

    @Test
    void createBooking_whenBookerIsEqualOwner_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDto));
    }

    @Test
    void createBooking_whenBookerIsNotValid_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(10000L, bookingDto));
    }

    @Test
    void approveBooking() {
        bookingService.approveBooking(1L, 1L, true);

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getStart(), equalTo(bookingDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(result.getItemId(), equalTo(bookingDto.getItemId()));
        assertThat(result.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(result.getItem().getName(), equalTo(bookingDto.getItem().getName()));
        assertThat(result.getItem().getDescription(), equalTo(bookingDto.getItem().getDescription()));
        assertThat(result.getItem().getAvailable(), equalTo(bookingDto.getItem().getAvailable()));
        assertThat(result.getItem().getRequestId(), equalTo(bookingDto.getItem().getRequestId()));
        assertThat(result.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(result.getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(result.getStatus(), equalTo("APPROVED"));
    }

    @Test
    void approveBooking_whenUserIsEqualOwner_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBooking_whenBookingIsNotValid_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(2L, 10000L, true));
    }

    @Test
    void approveBooking_whenBookingStatusIsApproved_thenReturnedBadRequest() {
        bookingService.approveBooking(1L, 1L, true);
        assertThrows(BadRequest.class,
                () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingById() {
        BookingDto result = bookingService.getBookingById(2L, 1L);
        assertThat(result.getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void getBookingById_whenUserIsNotOwner_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void getBookingById_whenBookingIsNotFound_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, 10000L, true));
    }

    @Test
    void findAllBookings() {
        List<BookingDto> result = bookingService.findAllBookings(1L, "ALL", 0, 10);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllBookingsPast() {
        List<BookingDto> result = bookingService.findAllBookings(1L, "PAST", 0, 10);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllBookingsCurrent() {
        List<BookingDto> result = bookingService.findAllBookings(1L, "CURRENT", 0, 10);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllBookingsFuture() {
        List<BookingDto> result = bookingService.findAllBookings(1L, "FUTURE", 0, 10);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllBookingsWaiting() {
        List<BookingDto> result = bookingService.findAllBookings(1L, "WAITING", 0, 10);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllBookingsRejected() {
        List<BookingDto> result = bookingService.findAllBookings(1L, "REJECTED", 0, 10);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllBookings_whenUserIsNotFound_thenReturnedNotFoundException() {
        Long wrongUserId = 10000L;
        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookings(wrongUserId, "ALL", 0, 10));
    }

    @Test
    void findAllBookingsOwner() {

        List<BookingDto> result = bookingService.findAllBookingsOwner(1L, "ALL", 0, 10);

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(result.get(0).getItemId(), equalTo(bookingDto.getItemId()));
        assertThat(result.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(bookingDto.getItem().getName()));
        assertThat(result.get(0).getItem().getDescription(), equalTo(bookingDto.getItem().getDescription()));
        assertThat(result.get(0).getItem().getAvailable(), equalTo(bookingDto.getItem().getAvailable()));
        assertThat(result.get(0).getItem().getRequestId(), equalTo(bookingDto.getItem().getRequestId()));
        assertThat(result.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(result.get(0).getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void findAllBookingsOwner_whenUserIsNotFound_thenReturnedNotFoundException() {
        Long wrongUserId = 10000L;
        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingsOwner(wrongUserId, "ALL", 0, 10));
    }

    @Test
    void findAllBookingsOwner_whenUnsupportedStatus_thenReturnedUnsupportedStatusException() {
        assertThrows(UnsupportedStatus.class,
                () -> bookingService.findAllBookingsOwner(1L, "UNSUPPORTED_STATUS", 0, 10));
    }
}