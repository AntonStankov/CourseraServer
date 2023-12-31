package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.enums.StateEnums;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.course.UserState;
import com.example.demo.service.enrollment.EnrollmentService;
import com.example.demo.service.quiz.QuizService;
import com.example.demo.service.secrets.SecretsService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.tab.TabsService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

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

    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher findByUserId(Long userId) {
            return TeacherService.super.findByUserId(userId);
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

    private TabsService tabsService = new TabsService() {
        @Override
        public Tab insertTab(Tab tab, Long courseId) {
            return TabsService.super.insertTab(tab, courseId);
        }

        @Override
        public List<Tab> findTabsByCourseId(Long courseId, Long studentId) {
            return TabsService.super.findTabsByCourseId(courseId, studentId);
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

    private QuizService quizService = new QuizService() {
        @Override
        public Quiz save(String quizName, Long courseId) {
            return QuizService.super.save(quizName, courseId);
        }

        @Override
        public Quiz getQuizByCourseId(Long courseId) {
            return QuizService.super.getQuizByCourseId(courseId);
        }

        @Override
        public Quiz getQuizById(Long quizId) {
            return QuizService.super.getQuizById(quizId);
        }

        @Override
        public Answers insertAnswer(Long questionId, String answer) {
            return QuizService.super.insertAnswer(questionId, answer);
        }

        @Override
        public Question insertQuestion(Long quizId, String question, String rightAnswer, int points) {
            return QuizService.super.insertQuestion(quizId, question, rightAnswer, points);
        }

        @Override
        public Question getQuestionById(Long questionId) {
            return QuizService.super.getQuestionById(questionId);
        }

        @Override
        public boolean checkQuestionInQuiz(Long quizId, Long questionId) {
            return QuizService.super.checkQuestionInQuiz(quizId, questionId);
        }

        @Override
        public List<Question> getQuestionsByQuizId(Long quizId, boolean teacher) {
            return QuizService.super.getQuestionsByQuizId(quizId, teacher);
        }
    };

    private String coursesImages = "src/main/resources/static/";

    String url = "https://localhost:8080/actuator/refresh";



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

    @PostMapping("/setPicture/{courseId}")
    public Course setPicture(@RequestParam MultipartFile file, @PathVariable Long courseId, HttpServletRequest httpServletRequest) throws IOException {
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course course = courseService.findById(courseId, null);
        if (!Objects.equals(course.getTeacher().getTeacher_id(), teacher.getTeacher_id())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the course teacher!");

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not an image.");
        }

        Path filePath = Paths.get(coursesImages, "course_" + course.getCourseId().toString() + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        courseService.setPicturePath(courseId, "course_" + course.getCourseId().toString() + "_" + file.getOriginalFilename());

        return courseService.findById(courseId, null);
    }


    @PostMapping("/sign/{courseId}")
    public Enrollment signCourse(@PathVariable Long courseId, HttpServletRequest httpServletRequest) throws ResponseStatusException{
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(principal.getUsername());

        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            if (enrollmentService.findEnrollmentByStudnentAndCourseIds(courseId, studentService.findStudentByUserId(user.getId()).getStudent_id()) != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already signed for this course!");
            return enrollmentService.save(studentService.findStudentByUserId(user.getId()).getStudent_id(), courseId);
        }
    }

    

    @PostMapping("/complete/{courseId}")
    public Enrollment completeCourse(@PathVariable Long courseId, HttpServletRequest httpServletRequest) throws ResponseStatusException{
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(principal.getUsername());

        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            Enrollment enrollment = enrollmentService.updateEnrollment(courseId, studentService.findStudentByUserId(user.getId()).getStudent_id());
            if (enrollment != null) return enrollment;
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already completed this course!");
        }
    }

    @GetMapping("/uncompleted")
    public PaginationResponse findUncompletedCourses(@RequestParam int page, @RequestParam int pageSize, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "JWT Token has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        System.out.println(user.getId());
        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            return courseService.findUncompletedCourses(studentService.findStudentByUserId(user.getId()).getStudent_id(), page, pageSize);
        }
    }

    @GetMapping("/completed")
    public PaginationResponse completed(@RequestParam int page, @RequestParam int pageSize, @RequestParam Boolean completed,  HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "JWT Token has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        if (!user.getRole().toString().equals("STUDENT")) throw new RuntimeException("You are not a student!");
        else {
            return courseService.findCompleteCourses(studentService.findStudentByUserId(user.getId()).getStudent_id(), page, pageSize, completed);
        }
    }

    @GetMapping("/findAll")
    public PaginationResponse findAll(@RequestParam int page, @RequestParam int pageSize, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "JWT Token has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("TEACHER")){
            return courseService.findAll(null, page, pageSize);
        }
        else return courseService.findAll(studentService.findStudentByUserId(user.getId()).getStudent_id(), page, pageSize);

    }

    @PostMapping("/edit")
    public Course editCourse(@RequestBody Course course, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course entity = courseService.findById(course.getCourseId(), null);
        if (!Objects.equals(entity.getTeacher().getTeacher_id(), teacher.getTeacher_id())) throw new RuntimeException("You are not the course teacher!");
        courseService.editCourse(course);
        return courseService.findById(course.getCourseId(), null);
    }

    @GetMapping("/findMyCourses") // get courses created by the teacher
    public PaginationResponse findMyCourses(@RequestParam int page, @RequestParam int pageSize, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        Teacher teacher = teacherService.findByUserId(user.getId());
        return courseService.findTeachersCourses(teacher.getTeacher_id(), page, pageSize);
    }

    @GetMapping("/findCourseById/{courseId}")
    public Object findCourseById(@PathVariable Long courseId, HttpServletRequest httpServletRequest){
        Course course = new Course();
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().equals(UserRoleEnum.STUDENT)) course = courseService.findByIdTabs(courseId, studentService.findStudentByUserId(user.getId()).getStudent_id());
        else course = courseService.findByIdTabs(courseId, null);

        if (user.getRole() == UserRoleEnum.TEACHER && course != null) return new CourseState(course, null);
        else if (user.getRole() == UserRoleEnum.STUDENT) {
            StateEnums stateEnum = null;
            UserState userState = courseService.checkEnrollment(courseId, studentService.findStudentByUserId(user.getId()).getStudent_id());
            if (userState.isEnrolled() && userState.isCompleted()) stateEnum = StateEnums.COMPLETED;
            else if (userState.isEnrolled() && !userState.isCompleted()){
                stateEnum = StateEnums.START_QUIZ;
                List<Tab> tabs = tabsService.findTabsByCourseId(courseId, studentService.findStudentByUserId(user.getId()).getStudent_id());
                for (int i = 0; i < tabs.size(); i++){
                    if (!tabs.get(i).isCompleted()) stateEnum = StateEnums.COMPLETE_TABS;
                }
                if (stateEnum == StateEnums.START_QUIZ && quizService.getQuizByCourseId(courseId) == null) {
                    enrollmentService.updateEnrollment(courseId, studentService.findStudentByUserId(user.getId()).getStudent_id());
                    stateEnum = StateEnums.COMPLETED;
                }
            }
            else if (!userState.isEnrolled()) stateEnum = StateEnums.CAN_ENROLL;
            return new CourseState(course, stateEnum);
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no course with id: " + courseId.toString());
    }

    @GetMapping("/findAllByName")
    public PaginationResponse findAllByName(@RequestParam int page, @RequestParam int pageSize, @RequestParam String name, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");

        return courseService.searchAllCoursesByName(page, pageSize, name);
    }
}
