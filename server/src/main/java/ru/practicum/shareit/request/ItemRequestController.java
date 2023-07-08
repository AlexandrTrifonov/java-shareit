package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.Variables;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findRequests(@RequestHeader(value = Variables.USER_ID) Long userId) {
        return itemRequestService.findItemRequestsOwner(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItem(@RequestHeader(value = Variables.USER_ID) Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequestById(@RequestHeader(value = Variables.USER_ID) Long userId,
                                         @PathVariable(value = "requestId") Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAllRequests(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                      @RequestParam(value = "from", defaultValue = "0") int from,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        return itemRequestService.findAllItemRequests(userId, from, size);
    }
}
