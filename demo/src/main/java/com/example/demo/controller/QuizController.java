package com.example.demo.controller;


import com.example.demo.entity.*;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.course.UserState;
import com.example.demo.service.enrollment.EnrollmentService;
import com.example.demo.service.quiz.QuizService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/quiz")
public class QuizController {

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

        @Override
        public User findUserByStudentId(Long studentId) {
            return UserService.super.findUserByStudentId(studentId);
        }

        @Override
        public User findUserByTeacherId(Long teacherId) {
            return UserService.super.findUserByTeacherId(teacherId);
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
        public Question insertQuestion(Long quizId, String question, String rightAnswer) {
            return QuizService.super.insertQuestion(quizId, question, rightAnswer);
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
        public Course findByIdTabs(Long courseId, Long studentId) {
            return CourseService.super.findByIdTabs(courseId, studentId);
        }

        @Override
        public PaginationResponse findUncompletedCourses(Long studentId, int page, int pageSize) {
            return CourseService.super.findUncompletedCourses(studentId, page, pageSize);
        }

        @Override
        public PaginationResponse findCompleteCourses(Long studentId, int page, int pageSize, Boolean completed) {
            return CourseService.super.findCompleteCourses(studentId, page, pageSize, completed);
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


    @PostMapping("/addQuestion/{courseId}")
    public Quiz addQuestion(@RequestBody List<Question> questions, @PathVariable Long courseId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().equals(UserRoleEnum.STUDENT)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a teacher!");
        Teacher teacher = teacherService.findByUserId(user.getId());
        Course course = courseService.findById(courseId, null);
        if (course.getTeacher().getTeacher_id() != teacher.getTeacher_id()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not teacher in this course!");
        Quiz quiz = quizService.getQuizByCourseId(courseId);
        if (quiz == null){
            quiz = quizService.save(null, courseId);
        }

        for (int i = 0; i < questions.size(); i++){
            Question question = quizService.insertQuestion(quiz.getQuiz_id(), questions.get(i).getQuestion(), questions.get(i).getRightAnswer());
            for (int j = 0; j < questions.get(i).getAnswers().size(); j++){
                quizService.insertAnswer(question.getQuestion_id(), questions.get(i).getAnswers().get(j).getAnswer());
            }
        }
        return quizService.getQuizById(quiz.getQuiz_id());
    }

    @GetMapping("/getQuestionById")
    public Question getQuestionById(@RequestParam Long courseId, @RequestParam Long questionId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (!user.getRole().equals(UserRoleEnum.STUDENT)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a student!");
        Student student = studentService.findStudentByUserId(user.getId());
        Course course = courseService.findById(courseId, null);
        Quiz quiz = quizService.getQuizByCourseId(courseId);
        if (enrollmentService.findEnrollmentByStudnentAndCourseIds(courseId, student.getStudent_id()) != null){
            if (quizService.checkQuestionInQuiz(quiz.getQuiz_id(), questionId)) return quizService.getQuestionById(questionId);
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no question with this id in this quiz!");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not in this course!");
    }

    @GetMapping("/getQuestions/{courseId}")
    public List<Question> getQuestionByCourseId(@PathVariable Long courseId, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        Course course = new Course();
        Student student = new Student();

        if (user.getRole().equals(UserRoleEnum.STUDENT)){
            student = studentService.findStudentByUserId(user.getId());
            course =  courseService.findById(courseId, student.getStudent_id());
        }
        else course = courseService.findById(courseId, null);

        if (user.getRole().equals(UserRoleEnum.TEACHER)){
            return quizService.getQuestionsByQuizId(quizService.getQuizByCourseId(courseId).getQuiz_id(), user.getRole().equals(UserRoleEnum.TEACHER));
        }
        else {
            if (enrollmentService.findEnrollmentByStudnentAndCourseIds(courseId, student.getStudent_id()) != null){
                return quizService.getQuestionsByQuizId(quizService.getQuizByCourseId(courseId).getQuiz_id(), false);
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not in this course!");
        }
    }
}
