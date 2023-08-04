package ru.yandex.practicum.filmorate.model.enums.converter;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.enums.EnumSortBy;

public class StringToEnumConverter implements Converter<String, EnumSortBy> {
    @Override
    public EnumSortBy convert(String source) {
        return EnumSortBy.valueOf(source.toUpperCase());
//
//        try {
//            return EnumSortBy.valueOf(source.toUpperCase());
//        } catch (RuntimeException e) {
//            throw new IncorrectParameterException("sortBy: " + source);
//        }
    }
}