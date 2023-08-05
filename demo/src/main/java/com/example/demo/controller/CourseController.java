package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.enrollment.EnrollmentService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private UserService userService = new UserService() {
        @Override
        public User save(User user) {
            return UserService.super.save(user);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }
    };

    private StudentService studentService = new StudentService() {
        @Override
        public Student save(Student student, User user) {
            return StudentService.super.save(student, user);
        }

        @Override
        public Student findById(Long id, String email) {
            return StudentService.super.findById(id, email);
        }

        @Override
        public Student findStudentByUserId(Long userId, String email) {
            return StudentService.super.findStudentByUserId(userId, email);
        }
    };

    private CourseService courseService = new CourseService() {
        @Override
        public Course create(Course course, Teacher teacher) {
            return CourseService.super.create(course, teacher);
        }
    };

    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher findByUserId(Long userId, String email) {
            return TeacherService.super.findByUserId(userId, email);
        }
    };


    private EnrollmentService enrollmentService = new EnrollmentService() {
        @Override
        public Enrollment save(Long studentId,
                               Long courseId) {
            return EnrollmentService.super.save(studentId, courseId);
        }
    };
    @PostMapping("/create")
    public Course create(@RequestBody Course course){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(principal.getUsername());

        if (!user.getRole().toString().equals("TEACHER")) throw new RuntimeException("You are not a teacher!");
        else {
            return courseService.create(course, teacherService.findByUserId(user.getId(), user.getEmail()));
        }
    }

    @PostMapping("/complete/{courseId}")
    public Enrollment completeCourse(@PathVariable Long courseId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(principal.getUsername());

        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            return enrollmentService.save(studentService.findStudentByUserId(user.getId(), user.getEmail()).getStudent_id(), courseId);
        }
    }
}
