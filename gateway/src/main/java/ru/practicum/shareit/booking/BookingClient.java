package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> createBooking(Long bookerId, BookingDto bookingDto) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, Boolean approved) {
        System.out.println("222222222222222222");
        System.out.println(userId);
        return patch("/" + bookingId + "?approved=" + approved, userId.longValue());
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId.longValue());
    }

    public ResponseEntity<Object> findAllBookingsUser(Long userId, String status, int from, int size) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("from", from);
        parameter.put("size", size);
        parameter.put("state", status);
        return get("?state={state}&from={from}&size={size}", userId.longValue(), parameter);
    }

    public ResponseEntity<Object> findAllBookingsOwner(Long userId, String status, int from, int size) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("from", from);
        parameter.put("size", size);
        parameter.put("state", status);
        return get("/owner?state={state}&from={from}&size={size}", userId.longValue(), parameter);
    }

/*    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }*/

/*    public ResponseEntity<Object> findAllBookingsUser(Long userId, String status, int from, int size) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("from", from);
        parameter.put("size", size);
        parameter.put("state", status);
        return get("?state={state}&from={from}&size={size}", userId, parameter);
    }

    public ResponseEntity<Object> findAllBookingsOwner(Long userId, String status, int from, int size) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("from", from);
        parameter.put("size", size);
        parameter.put("state", status);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameter);
    }*/

}