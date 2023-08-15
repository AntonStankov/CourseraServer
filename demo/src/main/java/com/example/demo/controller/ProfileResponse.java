package com.example.demo.controller;


import com.example.demo.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileResponse {
    private String name;
    private String email;
    private UserRoleEnum role;
    private LocalDateTime timeCreated;
    private Long credit;

    public ProfileResponse(String name, String email, UserRoleEnum role, LocalDateTime timeCreated){
        this.name = name;
        this.email = email;
        this.role = role;
        this.timeCreated = timeCreated;
    }
}
