package ru.netology.service;

public interface BlackListToken {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
