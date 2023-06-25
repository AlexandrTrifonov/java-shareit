package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toDto(itemRequest.getUser()),
                itemRequest.getCreated(),
                itemRequest.getItems().stream().map(ItemMapper::toDto).collect(Collectors.toList())
        );
    }
}
