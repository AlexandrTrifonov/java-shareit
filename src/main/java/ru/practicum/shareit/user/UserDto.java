package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
@AllArgsConstructor
@Data
public class UserDto {
    Long id;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$")
    @Size(max = 20, message = "максимальная длина имени - 20 символов")
    String name;
    @NotBlank
    @Email
    String email;
}
