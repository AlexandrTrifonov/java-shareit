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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryImpl itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userId == null) {
            log.warn("Ошибка - отсутствует id пользователя в запросе");
            throw new NotFoundException("Ошибка - отсутствует id пользователя");
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            log.warn("Ошибка - отсутствует пользователь");
            throw new NotFoundException("Ошибка - отсутствует пользователь");
        }
        Item item = ItemMapper.toItem(user, itemDto);
        item = itemRepository.createItem(userId, item);
        log.info("создан {}", item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        if (userId == null) {
            log.warn("Ошибка - отсутствует id пользователя в запросе");
            throw new NotFoundException("Ошибка - отсутствует id пользователя");
        }
        User userCheck = userRepository.getUserById(userId);
        List<Item> itemsByUser = itemRepository.itemsByUser(userId);
        if (userCheck == null || itemsByUser == null) {
            log.warn("Ошибка - отсутствует пользователь");
            throw new NotFoundException("Ошибка - отсутствует пользователь");
        }
        for (Item item : itemsByUser) { // обновление только item пользователя с userId
            if (Objects.equals(item.getId(), id)) {
                itemDto.setId(id);
                if (itemDto.getName() == null) itemDto.setName(item.getName());
                if (itemDto.getDescription() == null) itemDto.setDescription(item.getDescription());
                if (itemDto.getAvailable() == null) itemDto.setAvailable(item.getAvailable());

                itemsByUser.remove(itemRepository.getItemById(id));

                Item itemUpdate = ItemMapper.toItem(userCheck, itemDto);
                Item itemReturn = itemRepository.updateItem(userId, itemUpdate);
                log.info("создан {}", itemReturn);
                return ItemMapper.toDto(itemReturn);
            }
        }
        return null;
    }

    @Override
    public Collection<ItemDto> findAllItems(Long userId) {
        return itemRepository.findAllItems(userId).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toDto(itemRepository.getItemById(id));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemRepository.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
