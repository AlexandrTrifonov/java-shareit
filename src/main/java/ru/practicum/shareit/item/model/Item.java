package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}