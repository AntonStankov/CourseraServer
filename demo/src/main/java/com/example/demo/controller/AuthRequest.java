package com.example.demo.controller;


import com.example.demo.enums.UserRoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthRequest {
    @JsonProperty("email")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @JsonProperty("password")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;
    private String firstName;
    private String lastName;
    @JsonProperty("name")
    private String name;
    private UserRoleEnum role;
}
