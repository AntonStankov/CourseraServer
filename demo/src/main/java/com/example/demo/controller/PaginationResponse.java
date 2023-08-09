package com.example.demo.controller;


import com.example.demo.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationResponse {
    private Long size;
    private List<Course> courses;
}
