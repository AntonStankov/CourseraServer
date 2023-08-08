package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_secrets")
public class UserSecrets {

    @Id
    @OneToOne
    private User user_id;

    @Column(nullable = false)
    String password;
}
