package com.example.demo.controller;


import com.example.demo.enums.UserRoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    private String firstName;
    private String lastName;
    @JsonProperty("name")
    private String name;
    private UserRoleEnum role;
}
