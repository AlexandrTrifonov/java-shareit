package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> findAllItemsUser(Long userId) {
        log.info("Запрос на получение всех предметов");
        return get("", userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        log.info("Запрос на создание предмета");
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long id, ItemDto itemDto) {
        log.info("Запрос на обновление предмета");
        return patch("/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long id) {
        log.info("Запрос на получение предмета по Id");
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> search(Long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        log.info("Запрос на поиск");
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long authorId, Long itemId, CommentDto commentDto) {
        log.info("Запрос на добавление комментария");
        return post("/" + itemId + "/comment", authorId, commentDto);
    }
}
