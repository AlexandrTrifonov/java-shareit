package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$")
    @Size(max = 20, message = "максимальная длина имени - 20 символов")
    private String name;
    @NotBlank
    @Email
    private String email;
}
