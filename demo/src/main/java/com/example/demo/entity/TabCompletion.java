package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tabCompletion")
public class TabCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long completion_id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Tab tab;
}
