package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.enums.StateEnums;
import com.example.demo.service.course.UserState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseState {
    private Course course;
    private StateEnums stateEnum;
}
