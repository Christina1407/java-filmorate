package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friendship {
     private final Long friendshipId;
     private final EnumRelationType relationType;
     private final Long userId;
     private final Long friendId;
}
