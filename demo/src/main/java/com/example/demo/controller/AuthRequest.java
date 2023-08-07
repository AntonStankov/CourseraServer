package com.example.demo.controller;


import com.example.demo.enums.UserRoleEnum;
import lombok.Getter;

@Getter

public class AuthRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String name;
    private UserRoleEnum role;
}
