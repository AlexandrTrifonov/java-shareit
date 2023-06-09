package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userId == null) {
            log.warn("Ошибка - отсутствует id пользователя в запросе");
            throw new NotFoundException("Ошибка - отсутствует id пользователя");
        }
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            log.warn("Ошибка - отсутствует пользователь");
            throw new NotFoundException("Ошибка - отсутствует пользователь");
        }

        Item item = ItemMapper.toItem(user.get(), itemDto);
        item.setOwner(user.get());
        item.setRequestId(888);
        item = itemRepository.save(item);
        log.info("создан {}", item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        if (userId == null) {
            log.warn("Ошибка - отсутствует id пользователя в запросе");
            throw new NotFoundException("Ошибка - отсутствует id пользователя");
        }
        Optional<Item> itemOptional = itemRepository.findById(id);
        Item item = itemOptional.orElse(null);
        if (item != null && item.getOwner().getId().equals(userId)) {
            itemDto.setId(id);
            if (itemDto.getName() == null) itemDto.setName(item.getName());
            if (itemDto.getDescription() == null) itemDto.setDescription(item.getDescription());
            if (itemDto.getAvailable() == null) itemDto.setAvailable(item.getAvailable());
    //        if (itemDto.getRequestId() == null) itemDto.setRequestId(item.getRequestId()); //todo?

            Optional<User> user = userRepository.findById(userId);
            Item itemUpdate = ItemMapper.toItem(user.get(), itemDto);
            itemUpdate.setOwner(user.get());
            itemUpdate.setRequestId(888);
            Item itemReturn = itemRepository.save(itemUpdate);
            log.info("обновлен {}", itemReturn);
            return ItemMapper.toDto(itemReturn);
        } else {
            throw new NotFoundException("Тема не найдена");
        }
    }

    @Override
    public Collection<ItemDto> findAllItems(Long userId) {
        Optional<User> owner = userRepository.findById(userId);
        return itemRepository.findByOwner(owner).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            return ItemMapper.toDto(item.get());
        } else {
            throw new NotFoundException("Тема не найдена");
        }
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemRepository.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
