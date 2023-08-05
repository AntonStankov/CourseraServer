package com.example.demo.jwt;

import org.jvnet.hk2.annotations.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BlackListServiceImpl implements BlackListService{

    private Set<String> blacklistedTokens = new HashSet<>();
    @Override
    public void addTokenToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
