package com.example.demo.service.course;


import com.example.demo.controller.PaginationResponse;
import com.example.demo.entity.Course;
import com.example.demo.entity.Teacher;
import org.springframework.stereotype.Service;

@Service
public interface CourseService {
    CoursesTableManager coursesTableManager = new CoursesTableManager();

    public default Course create(Course course, Teacher teacher){
        return coursesTableManager.insertCourse(course, teacher);
    }

    public default Course findById(Long courseId, Long studentId){
        return coursesTableManager.getCourseById(courseId, studentId);
    }

    public default PaginationResponse findUncompletedCourses(Long userId, int page, int pageSize){
        return coursesTableManager.findUncompletedCourses(userId, page, pageSize);
    }

    public default PaginationResponse findCompleteCourses(Long studentId, int page, int pageSize, Boolean completed){
        return coursesTableManager.findCompletedCourses(studentId, page, pageSize, completed);
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

    public default PaginationResponse findTeachersCourses(Long teacherId, int page, int pageSize){
        return coursesTableManager.findTeachersCourses(teacherId, page, pageSize);
    }

    public default PaginationResponse searchAllCoursesByName(int page, int pageSize, String name){
        return coursesTableManager.searchAllCoursesByName(page, pageSize, name);
    }

    public default UserState checkEnrollment(Long courseId, Long studentId){
        return coursesTableManager.checkEnrollment(courseId, studentId);
    }
}
