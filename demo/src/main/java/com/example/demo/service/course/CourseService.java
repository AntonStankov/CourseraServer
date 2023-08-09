package com.example.demo.service.course;


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

    public default List<Course> findUncompleteCourses(Long userId, int page, int pageSize){
        return coursesTableManager.findUncompletedCourses(userId, page, pageSize);
    }
}
