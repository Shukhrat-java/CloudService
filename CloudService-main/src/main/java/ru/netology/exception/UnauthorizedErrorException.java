package ru.netology.exception;

public class UnauthorizedErrorException extends RuntimeException {
    public UnauthorizedErrorException(String message) {
        super(message);
    }
}
