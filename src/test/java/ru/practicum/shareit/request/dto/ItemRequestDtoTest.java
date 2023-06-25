package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestWithItems() throws Exception {
        LocalDateTime createdTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 3L, null, null, null);
        UserDto userDto = new UserDto();
        List<ItemDto> itemDtoList = List.of(itemDto);
        ItemRequestDto itemRequestDto = new ItemRequestDto(3L, "description", userDto, createdTime, itemDtoList);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(createdTime.toString());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(3);

    }
}