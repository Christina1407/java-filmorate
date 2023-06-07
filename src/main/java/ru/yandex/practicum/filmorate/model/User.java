package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {

    private Integer id;
    @Email
    private final String email;
    @NotEmpty
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;
}
