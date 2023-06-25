package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemRequestDto> findItemRequestsOwner(Long userId) {
        User user = getRequestorUser(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findByUser(user);
        List<ItemRequestDto> itemRequestDtoList = itemRequestList.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
        return sortItemRequestList(addItems(itemRequestDtoList));
    }

    private List<ItemRequestDto> addItems(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList.forEach(requestDto -> {
            List<Item> itemList = itemRepository.findByRequestId(requestDto.getId());
            List<ItemDto> itemDtoList = itemList.stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
            requestDto.setItems(itemDtoList);
        });
        return itemRequestDtoList;
    }

    private List<ItemRequestDto> sortItemRequestList(List<ItemRequestDto> itemRequestDtoList) {
        return itemRequestDtoList.stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        Optional<User> userCheck = userRepository.findById(userId);
        if (userCheck.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        ItemRequest itemRequest = new ItemRequest();
        User user = getRequestorUser(userId);
        itemRequest.setUser(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setItems(new ArrayList<>());
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    private User getRequestorUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        getRequestorUser(userId);
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Не найден запрос " + requestId);
        }
        return ItemRequestMapper.toDto(itemRequestRepository.findAllById(requestId));
    }

    @Override
    public List<ItemRequestDto> findAllItemRequests(Long userId, int from, int size) {
        getRequestorUser(userId);
        Sort sort = Sort.by("created");
        PageRequest page = PageRequest.of(from, size, sort);
        Page<ItemRequest> listItemRequest = itemRequestRepository.findAllByUserIdIsNot(userId, page);
        return listItemRequest.getContent().stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
