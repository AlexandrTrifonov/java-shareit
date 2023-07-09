package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> findItemRequestsOwner(Long userId);

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);

    List<ItemRequestDto> findAllItemRequests(Long userId, int from, int size);
}

