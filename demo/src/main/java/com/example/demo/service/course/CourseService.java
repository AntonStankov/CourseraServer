package com.example.demo.service.course;


import com.example.demo.controller.PaginationResponse;
import com.example.demo.entity.Course;
import com.example.demo.entity.Teacher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService {
    CoursesTableManager coursesTableManager = new CoursesTableManager();

    public default Course create(Course course, Teacher teacher){
        return coursesTableManager.insertCourse(course, teacher);
    }

    public default Course findById(Long courseId){
        return coursesTableManager.getCourseById(courseId);
    }

    public default PaginationResponse findUncompleteCourses(Long userId, int page, int pageSize){
        return coursesTableManager.findUncompletedCourses(userId, page, pageSize);
    }

    public default PaginationResponse findCompleteCourses(Long userId, int page, int pageSize, Boolean completed){
        return coursesTableManager.findCompletedCourses(userId, page, pageSize, completed);
    }

    public default PaginationResponse findAll(Long userId, int page, int pageSize){
        return coursesTableManager.findAllCourses(userId, page, pageSize);
    }

    public default void setPicturePath(Long courseId, String path){
        coursesTableManager.setImagePath(courseId, path);
    }

    public default void addStudentsCount(Long courseId){
        coursesTableManager.addStudentsCount(courseId);
    }

    public default void editCourse(Course course){
        coursesTableManager.updateCourse(course);
    }
}
