package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository; //
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(Long bookerId, BookingDto bookingDto) {

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            log.warn("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        checkDate(bookingDto);
        if (item.get().getAvailable().equals(false)) {
            log.warn("Вещь не доступна к брони");
            throw new BadRequest("Вещь не доступна к брони");
        }
        if (item.get().getOwner().getId().equals(bookerId)) {
            log.warn("Пользователь не может бронировать вещь");
            throw new NotFoundException("Пользователь не может бронировать вещь");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.warn("Начало и конец совпадают");
            throw new BadRequest("Начало и конец совпадают");
        }
        Optional<User> booker = userRepository.findById(bookerId);
        if (booker.isEmpty()) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item.get());
        booking.setBooker(booker.get());
        booking.setStatus(String.valueOf(Status.WAITING));
        bookingRepository.save(booking);
        log.info(String.valueOf(booking));
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long id, Boolean approved) {
        Optional<Booking> bookingOption = bookingRepository.findById(id);
        if (bookingOption.isPresent()) {
            User owner = bookingOption.get().getItem().getOwner();
            if (!owner.getId().equals(userId)) {
                log.warn("Пользователь не может подтвердить бронь");
                throw new NotFoundException("Пользователь не найден");
            }
        } else {
            log.warn("Ошибка Optional");
            throw new NotFoundException("Ошибка Optional");
        }
        Booking booking = bookingOption.get();
        if (booking.getStatus().equals(String.valueOf(Status.APPROVED))) {
            log.warn("Бронь уже подтверждена");
            throw new BadRequest("Бронь уже подтверждена");
        }
        if (approved) {
            booking.setStatus(String.valueOf(Status.APPROVED));
        } else {
            booking.setStatus(String.valueOf(Status.REJECTED));
        }
        bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long id) {
        Optional<Booking> bookingOption = bookingRepository.findById(id);
        if (bookingOption.isPresent()) {
            User owner = bookingOption.get().getItem().getOwner();
            User booker = bookingOption.get().getBooker();
            if (!(owner.getId().equals(userId) || booker.getId().equals(userId))) {
                log.warn("Пользователь не создатель вещи или брони");
                throw new NotFoundException("Пользователь не найден");
            }
            return BookingMapper.toDto(bookingOption.get());
        } else {
            log.warn("Бронь не найдена");
            throw new NotFoundException("Бронь не найдена");
        }
    }

    @Override
    public List<BookingDto> findAllBookings(Long userId, String state, int from, int size) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            List<Booking> listBookings;
            User booker = userOptional.get();
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
            Page<Booking> listPageBooking;
            listBookings = bookingRepository.findByBookerOrderByStartDesc(booker, page);
        //    listPageBooking = bookingRepository.findByBookerOrderByStartDesc(booker, page);
        //    listBookings = bookingRepository.findByBookerOrderByStartDesc(booker, page).getContent();
            State bookingState = checkBookingState(state);
            return sortedByState(listBookings, bookingState);
        //    return sortedByState(listPageBooking.getContent(), bookingState);
        //    return sortedByState(listBookings, bookingState);
        } else {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<BookingDto> findAllBookingsOwner(Long userId, String state, int from, int size) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            List<Item> itemsOwner = itemRepository.findByOwner(userOptional);
            List<Booking> bookingList = new ArrayList<>();
            itemsOwner.stream().forEach(item -> {
                        List<Booking> bookingsList = bookingRepository.findByItemIdOrderByStartDesc(item.getId());
                        bookingList.addAll(bookingsList);
                    }
            );
            State bookingState = checkBookingState(state);
            return sortedByState(bookingList, bookingState);
        } else {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void checkDate(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())
        ) {
            log.warn("Конец не после начала");
            throw new BadRequest("Ошибка даты");
        }
    }

    private List<BookingDto> sortedByState(List<Booking> bookingList, State state) {
        if (state.equals(State.CURRENT)) {
            return bookingList.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.PAST)) {
            return bookingList.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.FUTURE)) {
            return bookingList.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                            && booking.getEnd().isAfter(LocalDateTime.now()))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.WAITING)) {
            return bookingList.stream()
                    .filter(booking -> booking.getStatus().equals(String.valueOf(Status.WAITING)))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(State.REJECTED)) {
            return bookingList.stream()
                    .filter(booking -> booking.getStatus().equals(String.valueOf(Status.REJECTED)))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        return bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private State checkBookingState(String state) {
        try {
            return State.valueOf(state);
        } catch (Throwable e) {
            throw new UnsupportedStatus("Unknown state: " + state);
        }
    }
}
