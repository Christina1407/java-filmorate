package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.RatingMPAController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {FilmController.class, UserController.class, FilmService.class, UserService.class, RatingMPAController.class, GenreController.class})
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException e) {
        return new ExceptionResponse("Искомый объект не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final AlreadyExistsException e) {
        return new ExceptionResponse("Искомый объект уже существует");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ExceptionValidationResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIncorrectParameterException(final IncorrectParameterException e) {
        return new ExceptionResponse("Ошибка с полем " + e.getParameter());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(final Exception e) {
        return new ExceptionResponse("Непредвиденная ошибка");
    }


}

