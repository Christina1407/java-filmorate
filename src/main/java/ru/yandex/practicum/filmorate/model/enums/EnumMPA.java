package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumMPA {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String name;

    EnumMPA(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    //    G — у фильма нет возрастных ограничений,
//    PG — детям рекомендуется смотреть фильм с родителями,
//    PG-13 — детям до 13 лет просмотр не желателен,
//    R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
//    NC-17 — лицам до 18 лет просмотр запрещён
}
