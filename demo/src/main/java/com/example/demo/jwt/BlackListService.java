package com.example.demo.jwt;


public interface BlackListService {
    public void addTokenToBlacklist(String token);
    public boolean isTokenBlacklisted(String token);
}
