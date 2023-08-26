package com.example.demo;

import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.entity.Course;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.course.CoursesTableManager;
import com.example.demo.service.user.UserService;
import jakarta.persistence.EnumType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class DemoApplicationTests {


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
	@Test
	void contextLoads() {
	}

    @DisplayName("Should be able to add student")
    @Test
    void addStudent(){
        User user = new User(null, "a@gmail.com", UserRoleEnum.STUDENT, LocalDateTime.now());
        userService.save(user, "12345678");
        Student student = new Student(null, "Anton", null, 0L);

        String string = studentService.save(student, userService.findByEmail(user.getEmail())).getName();
        String expected = "Anton";
        assertThat(string).isEqualTo(expected);
    }

    @DisplayName("Should be able to add teacher")
    @Test
    void addTeacher(){
        User user = new User(null, "a1@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
        userService.save(user, "12345678");
        Teacher teacher = new Teacher(null, "Anton", null);

        String string = teacherService.save(teacher, userService.findByEmail(user.getEmail())).getName();
        String expected = "Anton";
        assertThat(string).isEqualTo(expected);
    }
//    @Test
//    void addStudent(){
//        User user = new User(null, "a@gmail.com", UserRoleEnum.STUDENT, LocalDateTime.now());
//        userService.save(user, "12345678");
//        Student student = new Student(null, "Anton", null, 0L);
//
//        String string = studentService.save(student, userService.findByEmail(user.getEmail())).getName();
//        String expected = "Anton";
//        assertThat(string).isEqualTo(expected);
//    }
//
//    @Test
//    void addTeacher(){
//        User user = new User(null, "a1@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
//        userService.save(user, "12345678");
//        Teacher teacher = new Teacher(null, "Anton", null);
//
//        String string = teacherService.save(teacher, userService.findByEmail(user.getEmail())).getName();
//        String expected = "Anton";
//        assertThat(string).isEqualTo(expected);
//    }

    @DisplayName("Should be able to create course")
    @Test
    void createCourse()
    {
        CoursesTableManager coursesTableManager = new CoursesTableManager();
        User newUser = new User(Long.valueOf(12),
                "a@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
        Teacher newTeacher = new Teacher(Long.valueOf(10), "TeacherName", newUser);
        teacherService.save(newTeacher, userService.findByEmail(newUser.getEmail()));
        Course newCourse = new Course(Long.valueOf(10), newTeacher, "Dropwizard Course", 20);

        String courseName = coursesTableManager.insertCourse(newCourse, newTeacher).getCourseName();
        String expected_courseName = "Dropwizard Course";
        assertThat(expected_courseName).isEqualTo(courseName);

        String courseTeachName = coursesTableManager.insertCourse(newCourse, newTeacher).getTeacher().getName();
        String expected_courseTeachName = "TeacherName";
        assertThat(expected_courseTeachName).isEqualTo(courseTeachName);
    }

    @DisplayName("Should not be able to create course without name")
    @Test
    void cannotCreateCourseWithNullName()
    {
        CoursesTableManager coursesTableManager = new CoursesTableManager();
        User newUser = new User(Long.valueOf(12),
                "a@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
        Teacher newTeacher = new Teacher(Long.valueOf(10), "TeacherName", newUser);
        teacherService.save(newTeacher, userService.findByEmail(newUser.getEmail()));
        Course newCourse = new Course(Long.valueOf(10), newTeacher, null, 20);

        coursesTableManager.insertCourse(newCourse, newTeacher);
        assertThat(newCourse.equals(null));
    }

    @DisplayName("Should be able to get a course by ID")
    @Test
    void courseByID()
    {
        CoursesTableManager coursesTableManager = new CoursesTableManager();
        User newUser = new User(Long.valueOf(12),
                "a@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
        Teacher newTeacher = new Teacher(Long.valueOf(10), "TeacherName", newUser);
        teacherService.save(newTeacher, userService.findByEmail(newUser.getEmail()));
        Course newCourse = new Course(Long.valueOf(10), newTeacher, "Dropwizard Course", 20);

        coursesTableManager.insertCourse(newCourse, newTeacher);

        String courseName = coursesTableManager.getCourseById(Long.valueOf(10)).getCourseName();

        String expected_courseName = "Dropwizard Course";
        assertThat(expected_courseName).isEqualTo(courseName);
    }

    @DisplayName("Should not be able to get course with ID that doesn't exist")
    @Test
    void cannotGetCourseByIDIfItDoesntExist()
    {
        CoursesTableManager coursesTableManager = new CoursesTableManager();
        User newUser = new User(Long.valueOf(12),
                "a@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
        Teacher newTeacher = new Teacher(Long.valueOf(10), "TeacherName", newUser);
        teacherService.save(newTeacher, userService.findByEmail(newUser.getEmail()));
        Course newCourse = new Course(Long.valueOf(10), newTeacher, "Dropwizard Course", 20);

        coursesTableManager.insertCourse(newCourse, newTeacher);

        Course gotCourse = coursesTableManager.getCourseById(Long.valueOf(1000));

        assertThat(gotCourse).isEqualTo(null);
    }

    @DisplayName("Should not be able to get course with ID == null")
    @Test
    void cannotGetCourseByIDIfIDIsNull()
    {
        CoursesTableManager coursesTableManager = new CoursesTableManager();
        User newUser = new User(Long.valueOf(12),
                "a@gmail.com", UserRoleEnum.TEACHER, LocalDateTime.now());
        Teacher newTeacher = new Teacher(Long.valueOf(10), "TeacherName", newUser);
        teacherService.save(newTeacher, userService.findByEmail(newUser.getEmail()));
        Course newCourse = new Course(Long.valueOf(10), newTeacher, "Dropwizard Course", 20);

        coursesTableManager.insertCourse(newCourse, newTeacher);

        Course gotCourse = coursesTableManager.getCourseById(null);

        assertThat(gotCourse).isEqualTo(null);
    }



}


