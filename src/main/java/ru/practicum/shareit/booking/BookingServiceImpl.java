package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
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
        booking.setStatus("WAITING");
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
        }
        Booking booking = bookingOption.get();
        if (booking.getStatus().equals("APPROVED")) {
            log.warn("Бронь уже подтверждена");
            throw new BadRequest("Бронь уже подтверждена");
        }
        if (approved) {
            booking.setStatus("APPROVED");
        } else {
            booking.setStatus("REJECTED");
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
    public List<BookingDto> findAllBookings(Long userId, String state) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            log.warn("Неверный статус");
            throw new UnsupportedStatus("Unknown state: UNSUPPORTED_STATUS");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            List<Booking> listBookings;
            User booker = userOptional.get();
            listBookings = bookingRepository.findByBookerOrderByStartDesc(booker);
            if (state.equals("CURRENT")) {
                return listBookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toDto)
        //                .sorted(new BookingComparator())
        //                .sorted(new BookingComparator().reversed())
                        .collect(Collectors.toList());
            }
            if (state.equals("PAST")) {
                return listBookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toDto)
        //                .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("FUTURE")) {
                return listBookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toDto)
        //                .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("WAITING")) {
                return listBookings.stream()
                        .filter(booking -> booking.getStatus().equals("WAITING"))
                        .map(BookingMapper::toDto)
        //                .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("REJECTED")) {
                return listBookings.stream()
                        .filter(booking -> booking.getStatus().equals("REJECTED"))
                        .map(BookingMapper::toDto)
        //                .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            return listBookings.stream()
                    .map(BookingMapper::toDto)
        //            .sorted(new BookingComparator())
                    .collect(Collectors.toList());

        } else {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<BookingDto> findAllBookingsOwner(Long userId, String state) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            log.warn("Неверный статус");
            throw new UnsupportedStatus("Unknown state: UNSUPPORTED_STATUS");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            List<Item> itemsOwner = itemRepository.findByOwner(userOptional);
            List<Booking> bookingList = new ArrayList<>();
            itemsOwner.stream().forEach(item -> {
                        List<Booking> bookingsList = bookingRepository.findByItemId(item.getId());
                        bookingList.addAll(bookingsList);
                    }
            );
            if (state.equals("CURRENT")) {
                return bookingList.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toDto)
                        .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("PAST")) {
                return bookingList.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toDto)
                        .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("FUTURE")) {
                return bookingList.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toDto)
                        .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("WAITING")) {
                return bookingList.stream()
                        .filter(booking -> booking.getStatus().equals("WAITING"))
                        .map(BookingMapper::toDto)
                        .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            if (state.equals("REJECTED")) {
                return bookingList.stream()
                        .filter(booking -> booking.getStatus().equals("REJECTED"))
                        .map(BookingMapper::toDto)
                        .sorted(new BookingComparator())
                        .collect(Collectors.toList());
            }
            return bookingList.stream()
                    .map(BookingMapper::toDto).sorted(new BookingComparator()).collect(Collectors.toList());
        } else {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public static class BookingComparator implements Comparator<BookingDto> {
        public int compare(BookingDto a, BookingDto b) {
            return b.getStart().compareTo(a.getStart());
        }
    }

    private void checkDate(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())
        ) {
            log.warn("Конец не после начала");
            throw new BadRequest("Ошибка даты");
        }
    }
}
