package ru.netology.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.ErrorResponse;
import ru.netology.exception.*;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse badRequestException(BadRequestException exception) {
        return new ErrorResponse(exception.getMessage(), 400);
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedErrorException.class)
    public ErrorResponse unauthorizedErrorException(UnauthorizedErrorException exception) {
        return new ErrorResponse(exception.getMessage(), 401);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerErrorException.class)
    public ErrorResponse internalServerErrorException(InternalServerErrorException exception) {
        return new ErrorResponse(exception.getMessage(), 500);
    }

}
