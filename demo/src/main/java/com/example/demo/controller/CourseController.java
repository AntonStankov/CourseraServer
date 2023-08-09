package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.enrollment.EnrollmentService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private JwtTokenService jwtTokenUtil;

    private UserService userService = new UserService() {
        @Override
        public User save(User user, String password) {
            return UserService.super.save(user, password);
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
        public Student findById(Long id) {
            return StudentService.super.findById(id);
        }

        @Override
        public Student findStudentByUserId(Long userId) {
            return StudentService.super.findStudentByUserId(userId);
        }
    };

    private CourseService courseService = new CourseService() {
        @Override
        public Course create(Course course, Teacher teacher) {
            return CourseService.super.create(course, teacher);
        }

        @Override
        public Course findById(Long courseId) {
            return CourseService.super.findById(courseId);
        }

        @Override
        public List<Course> findUncompleteCourses(Long userId, int page, int pageSize) {
            return CourseService.super.findUncompleteCourses(userId, page, pageSize);
        }
    };

    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher findByUserId(Long userId) {
            return TeacherService.super.findByUserId(userId);
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
    public Course create(@RequestBody Course course, HttpServletRequest httpServletRequest){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails principal = (UserDetails) authentication.getPrincipal();
//        User user = userService.findByEmail(principal.getUsername());
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));

        if (!user.getRole().toString().equals("TEACHER")) throw new RuntimeException("You are not a teacher!");
        else {
            return courseService.create(course, teacherService.findByUserId(user.getId()));
        }
    }

    @PostMapping("/complete/{courseId}")
    public Enrollment completeCourse(@PathVariable Long courseId, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(principal.getUsername());

        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            return enrollmentService.save(studentService.findStudentByUserId(user.getId()).getStudent_id(), courseId);
        }
    }

    @GetMapping("/uncompleted")
    public List<Course> findUncompletedCourses(@RequestBody PaginationRequest paginationRequest, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        System.out.println(user.getId());
        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            return courseService.findUncompleteCourses(studentService.findStudentByUserId(user.getId()).getStudent_id(), paginationRequest.getPage(), paginationRequest.getPageSize());
        }
    }

    @GetMapping("test")
    public Teacher test(HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        Teacher teacher = teacherService.findByUserId(user.getId());
        return teacher;
    }
}
