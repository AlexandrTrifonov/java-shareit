package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> findRequests(Long userId) {
        log.info("Запрос на получение запросов пользователя");
        return get("", userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Запрос на создание запроса");
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long requestId) {
        log.info("Запрос на получение запроса");
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAllRequests(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        log.info("Запрос на получение всех запросов");
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
