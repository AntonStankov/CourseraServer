package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.enums.TabContentType;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.course.UserState;
import com.example.demo.service.enrollment.EnrollmentService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.tab.TabsService;
import com.example.demo.service.tabCompletion.TabCompletionService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.tainting.qual.PolyTainted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        public Course findById(Long courseId, Long studentId) {
            return CourseService.super.findById(courseId, studentId);
        }

        @Override
        public PaginationResponse findUncompletedCourses(Long studentId, int page, int pageSize) {
            return CourseService.super.findUncompletedCourses(studentId, page, pageSize);
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

    private TabsService tabsService =  new TabsService() {
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
        public Tab findById(Long tabId, Long studentId) {
            return TabsService.super.findById(tabId, studentId);
        }

        @Override
        public boolean checkTabInCourse(Long courseId, Long tabId) {
            return TabsService.super.checkTabInCourse(courseId, tabId);
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

        @Override
        public Student changeName(String name, Long id) {
            return StudentService.super.changeName(name, id);
        }

        @Override
        public void addCredit(Long studentId, int credit) {
            StudentService.super.addCredit(studentId, credit);
        }
    };

    private TabCompletionService tabCompletionService = new TabCompletionService() {
        @Override
        public TabCompletion insertTabCompletion(Long studentId, Long tabId) {
            return TabCompletionService.super.insertTabCompletion(studentId, tabId);
        }
    };

    private EnrollmentService enrollmentService = new EnrollmentService() {
        @Override
        public Enrollment save(Long studentId, Long courseId) {
            return EnrollmentService.super.save(studentId, courseId);
        }

        @Override
        public Enrollment updateEnrollment(Long courseId, Long studentId) {
            return EnrollmentService.super.updateEnrollment(courseId, studentId);
        }

        @Override
        public Long findEnrollmentByStudnentAndCourseIds(Long courseId, Long studentId) {
            return EnrollmentService.super.findEnrollmentByStudnentAndCourseIds(courseId, studentId);
        }
    };
//    String tabName;
//    private TabContentType contentType;
//    private String content;
//    private Long courseId;
//    private MultipartFile file;

    @PostMapping("/create")
    public Tab createTab(@RequestParam String tabName,
                         @RequestParam TabContentType contentType,
                         @RequestParam String content,
                         @RequestParam Long courseId,
                         @RequestParam MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("STUDENT")) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not a teacher!");
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course course = courseService.findById(courseId, null);
        if (course.getTeacher().getTeacher_id() != teacher.getTeacher_id()) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not the course teacher!");
        Tab tab = new Tab();
        tab.setTabName(tabName);
        tab.setContentType(contentType);
        Course course1 = new Course();
        course1.setCourseId(courseId);
        tab.setCourse(course1);
        if (contentType.toString().equals("TEXT")){
            tab.setContent(content);
        }
        else {
            Path filePath = Paths.get("src/main/resources/static/", "tab_" + tabName + courseId + "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            tab.setContent("tab_" + tabName + courseId + "_" + file.getOriginalFilename());
        }
        return tabsService.insertTab(tab, course.getCourseId());
    }

    @PostMapping("/edit/{tabId}")
    public Tab editTab(@RequestBody Tab tab, @PathVariable Long tabId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("STUDENT")) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not a teacher!");
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course course = courseService.findById(tab.getCourse().getCourseId(), null);
        if (course.getTeacher().getTeacher_id() != teacher.getTeacher_id()) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not the course teacher!");
//        if (!course.getTabs().contains(tabsService.findById(tabId))) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "This tab is not in this course!");
        return tabsService.editTab(tab, tabId);
    }

    @PostMapping("/complete/{courseId}/{tabId}")
    public TabCompletion completeTab(@PathVariable Long courseId, @PathVariable Long tabId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (!user.getRole().toString().equals("STUDENT")) throw new ResponseStatusException(HttpStatusCode.valueOf(400), "You are not a student!");
        Student student = studentService.findStudentByUserId(user.getId());

        Tab tab = tabsService.findById(tabId, student.getStudent_id());
        Course course = courseService.findById(courseId, student.getStudent_id());
        if (enrollmentService.findEnrollmentByStudnentAndCourseIds(course.getCourseId(), student.getStudent_id()) == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not in this course!");
        if (!tabsService.checkTabInCourse(course.getCourseId(), tabId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This tab is not in this course!");
        return tabCompletionService.insertTabCompletion(student.getStudent_id(), tabId);

    }

    @GetMapping("/get/{courseId}/{tabId}")
    public Tab getTabById(@PathVariable Long courseId, @PathVariable Long tabId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        Student student = studentService.findStudentByUserId(user.getId());
        Course course = courseService.findById(courseId, student.getStudent_id());
        if (enrollmentService.findEnrollmentByStudnentAndCourseIds(courseId, student.getStudent_id()) == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not in this course!");
        if (!tabsService.checkTabInCourse(course.getCourseId(), tabId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This tab is not in this course!");
        return tabsService.findById(tabId, student.getStudent_id());
    }
}
