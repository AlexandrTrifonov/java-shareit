package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        CommentDto commentDto = new CommentDto(
                1L,
                "text",
                "authorName",
                created);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("authorName");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }
}