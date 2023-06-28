package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
        //    item.setRequestId(888);
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
            if (itemDto.getRequestId() != null) item.setRequestId(itemDto.getRequestId());
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Item itemUpdate = ItemMapper.toItem(user.get(), itemDto);
                itemUpdate.setOwner(user.get());
                Item itemReturn = itemRepository.save(itemUpdate);
                log.info("обновлен {}", itemReturn);
                return ItemMapper.toDto(itemReturn);
            } else {
                throw new NotFoundException("Пользователь не найден");
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        Optional<User> owner = userRepository.findById(userId);
        List<Item> itemList = itemRepository.findByOwnerOrderById(owner);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            ItemDto itemDto = ItemMapper.toDto(item);
            Booking next = bookingRepository.getNextBooking(item.getId(), LocalDateTime.now(), Constants.STATUS_APPROVED).orElse(null);
            Booking last = bookingRepository.getLastBooking(item.getId(), LocalDateTime.now(), Constants.STATUS_APPROVED).orElse(null);
            if (next != null) {
                itemDto.setNextBooking(BookingMapper.toDto(next));
            }
        //    else itemDto.setNextBooking(null);
            if (last != null) {
                itemDto.setLastBooking(BookingMapper.toDto(last));
            }
        //    else itemDto.setLastBooking(null);
            List<CommentDto> comments = commentRepository.getCommentsForItem(itemDto.getId()).stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Override
    public ItemDto getItem(Long userId, Long id) {
        User userCheck = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден")); //todo
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            ItemDto itemDto = ItemMapper.toDto(item.get());
            if (item.get().getOwner().getId().equals(userId)) {
                Booking next = bookingRepository.getNextBooking(item.get().getId(), LocalDateTime.now(), Constants.STATUS_APPROVED).orElse(null);
                Booking last = bookingRepository.getLastBooking(item.get().getId(), LocalDateTime.now(), Constants.STATUS_APPROVED).orElse(null);
                if (next != null) {
                    itemDto.setNextBooking(BookingMapper.toDto(next));
                }
            //    else itemDto.setNextBooking(null);
                if (last != null) {
                    itemDto.setLastBooking(BookingMapper.toDto(last));
                }
            //    else itemDto.setLastBooking(null);
            }
            List<CommentDto> comments = commentRepository.getCommentsForItem(itemDto.getId()).stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
            return itemDto;
        } else {
            throw new NotFoundException("Тема не найдена");
        }
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
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {
        Optional<User> authorOption = userRepository.findById(authorId);
        if (authorOption.isEmpty()) {
            throw new NotFoundException("Автор не найден");
        }
        User author = authorOption.get();
        Optional<Item> itemOption = itemRepository.findById(itemId);
        if (itemOption.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        Item item = itemOption.get();
        String bookingStatus = bookingRepository.checkStatusBooking(authorId, itemId, LocalDateTime.now());
        if (bookingStatus != null) {
            Comment comment = new Comment();
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            comment.setItem(item);
            comment.setText(commentDto.getText());
            Comment saveComment = commentRepository.save(comment);
            return CommentMapper.toDto(saveComment);
        } else {
            throw new BadRequest("Неверные параметры");
        }
    }
}
