package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @ManyToOne
    private Teacher teacher;

    @Column
    private String courseName;

    @Column
    private String description;

    @Column
    private Long duration;

    @Column
    private int credit;

    @Column
    private String picturePath;

    @Column
    private Long studentsCount;

    @OneToMany
    private List<Tab> tabs;
}
