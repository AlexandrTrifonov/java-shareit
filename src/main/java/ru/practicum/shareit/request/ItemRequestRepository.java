package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByUser(User user);

    ItemRequest findAllById(Long requestId);

    Page<ItemRequest> findAllByUserIdIsNot(Long userId, Pageable page);
}
