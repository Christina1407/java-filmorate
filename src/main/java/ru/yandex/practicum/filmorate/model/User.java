package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @NotEmpty(message = "email is empty")
    @Email(message = "email is not well-formed email address")
    private final String email;
    @NotEmpty(message = "login is empty")
    @Pattern(regexp = "\\S+", message = "login with whitespaces")
    private final String login;
    private String name;
    @Past(message = "birthday is not in past")
    private final LocalDate birthday;

    Set<Long> friendsId = new HashSet<>();

    public Boolean addFriend(Long userId) {
        return friendsId.add(userId);
    }

    public Boolean deleteFriend(Long userId) {
        return friendsId.remove(userId);
    }

}
