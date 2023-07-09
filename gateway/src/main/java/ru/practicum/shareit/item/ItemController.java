package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Variables;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllItemsUser(@RequestHeader(value = Variables.USER_ID) Long userId) {
        log.info("Запрос к клиенту на получение всех предметов");
        return itemClient.findAllItemsUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = Variables.USER_ID) Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос к клиенту на создание предмета");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = Variables.USER_ID) Long userId,
                                             @PathVariable(value = "itemId") Long id,
                                             @RequestBody ItemDto itemDto) {
        log.info("Запрос к клиенту на обновление предмета");
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(value = Variables.USER_ID) Long userId,
                                              @PathVariable(value = "itemId") Long id) {
        log.info("Запрос к клиенту на получение предмета по Id");
        return itemClient.getItemById(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(value = Variables.USER_ID) Long userId,
                                         @RequestParam("text") String text) {
        log.info("Запрос к клиенту на поиск");
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(value = Variables.USER_ID) Long authorId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос к клиенту на добавление комментария");
        return itemClient.createComment(authorId, itemId, commentDto);
    }
}
