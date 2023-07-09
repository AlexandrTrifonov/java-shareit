package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.Variables;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findRequests(@RequestHeader(value = Variables.USER_ID) Long userId) {
        log.info("Запрос к клиенту на получение запросов пользователя");
        return itemRequestClient.findRequests(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = Variables.USER_ID) Long userId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Запрос к клиенту на создание запроса");
        return itemRequestClient.createItem(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                 @PathVariable(value = "requestId") Long requestId) {
        log.info("Запрос к клиенту на получение запроса");
        return itemRequestClient.getItemById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                  @RequestParam(value = "from", defaultValue = "0") int from,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Запрос к клиенту на получение всех запросов");
        return itemRequestClient.findAllRequests(userId, from, size);
    }
}
