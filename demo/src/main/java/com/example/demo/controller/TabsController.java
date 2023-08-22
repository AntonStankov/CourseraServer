package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.Tab;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.course.UserState;
import com.example.demo.service.tab.TabsService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.tainting.qual.PolyTainted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tabs")
public class TabsController {

    @Autowired
    private JwtTokenService jwtTokenService;


    private UserService userService = new UserService() {
        @Override
        public User save(User user, String password) {
            return UserService.super.save(user, password);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }

        @Override
        public User findUserById(Long id) {
            return UserService.super.findUserById(id);
        }

        @Override
        public void deleteUser(Long id) {
            UserService.super.deleteUser(id);
        }

        @Override
        public User changeEmil(Long id, String email) {
            return UserService.super.changeEmil(id, email);
        }

        @Override
        public void setProfilePic(String path, Long userId) {
            UserService.super.setProfilePic(path, userId);
        }
    };

    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher save(Teacher teacher, User user) {
            return TeacherService.super.save(teacher, user);
        }

        @Override
        public Teacher findById(Long id) {
            return TeacherService.super.findById(id);
        }

        @Override
        public Teacher findByUserId(Long userId) {
            return TeacherService.super.findByUserId(userId);
        }

        @Override
        public Teacher updateTeacher(String name, Long id) {
            return TeacherService.super.updateTeacher(name, id);
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
        public PaginationResponse findUncompletedCourses(Long userId, int page, int pageSize) {
            return CourseService.super.findUncompletedCourses(userId, page, pageSize);
        }

        @Override
        public PaginationResponse findCompleteCourses(Long userId, int page, int pageSize, Boolean completed) {
            return CourseService.super.findCompleteCourses(userId, page, pageSize, completed);
        }

        @Override
        public PaginationResponse findAll(Long userId, int page, int pageSize) {
            return CourseService.super.findAll(userId, page, pageSize);
        }

        @Override
        public void setPicturePath(Long courseId, String path) {
            CourseService.super.setPicturePath(courseId, path);
        }

        @Override
        public void addStudentsCount(Long courseId) {
            CourseService.super.addStudentsCount(courseId);
        }

        @Override
        public void editCourse(Course course) {
            CourseService.super.editCourse(course);
        }

        @Override
        public PaginationResponse findTeachersCourses(Long teacherId, int page, int pageSize) {
            return CourseService.super.findTeachersCourses(teacherId, page, pageSize);
        }

        @Override
        public PaginationResponse searchAllCoursesByName(int page, int pageSize, String name) {
            return CourseService.super.searchAllCoursesByName(page, pageSize, name);
        }

        @Override
        public UserState checkEnrollment(Long courseId, Long studentId) {
            return CourseService.super.checkEnrollment(courseId, studentId);
        }
    };

    private TabsService tabsService = new TabsService() {
        @Override
        public Tab insertTab(Tab tab, Long courseId) {
            return TabsService.super.insertTab(tab, courseId);
        }

        @Override
        public List<Tab> findTabsByCourseId(Long courseId) {
            return TabsService.super.findTabsByCourseId(courseId);
        }

        @Override
        public Tab editTab(Tab tab, Long id) {
            return TabsService.super.editTab(tab, id);
        }

        @Override
        public Tab findById(Long id) {
            return TabsService.super.findById(id);
        }
    };

    @PostMapping("/create")
    public Tab createTab(@RequestBody Tab tab, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("STUDENT")) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not a teacher!");
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course course = courseService.findById(tab.getCourse().getCourseId());
        if (course.getTeacher().getTeacher_id() != teacher.getTeacher_id()) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not the course teacher!");
        return tabsService.insertTab(tab, course.getCourseId());
    }

    @PostMapping("/edit/{tabId}")
    public Tab editTab(@RequestBody Tab tab, @PathVariable Long tabId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("STUDENT")) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not a teacher!");
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course course = courseService.findById(tab.getCourse().getCourseId());
        if (course.getTeacher().getTeacher_id() != teacher.getTeacher_id()) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not the course teacher!");
//        if (!course.getTabs().contains(tabsService.findById(tabId))) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "This tab is not in this course!");
        return tabsService.editTab(tab, tabId);
    }
}
