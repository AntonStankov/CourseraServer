package com.example.demo.service.course;


import com.example.demo.entity.Course;
import com.example.demo.entity.Teacher;
import org.springframework.stereotype.Service;

@Service
public interface CourseService {
    CoursesTableManager coursesTableManager = new CoursesTableManager();

    public default Course create(Course course, Teacher teacher){
        return coursesTableManager.insertCourse(course, teacher);
    }

    public default Course findById(Long courseId, Long userId, String email){
        return coursesTableManager.getCourseById(courseId, userId, email);
    }
}
