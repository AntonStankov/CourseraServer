package com.example.demo.service.rating;

import com.example.demo.config.DataSource;
import com.example.demo.controller.PaginationResponse;
import com.example.demo.entity.*;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.course.UserState;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.user.UserService;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.*;

@NoArgsConstructor(force = true)
@Repository
public class RatingTableManager {
    private final DataSource datasource =  new DataSource();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public Rating insertRating(Long student_id, Long course_id, double rating) {
        Long generatedRatingId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO rating (student_id, course_id, rating) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setLong(1, student_id);
                preparedStatement.setLong(2, course_id);
                preparedStatement.setDouble(3, rating);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedRatingId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getRatingById(generatedRatingId);
    }


    public Rating getRatingById(Long id) {
        Rating rating = new Rating();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM rating WHERE rating_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        rating.setRating_id(id);
                        rating.setRating(resultSet.getDouble("rating"));
                        rating.setStudent(studentService.findById(resultSet.getLong("student_id")));
                        rating.setCourse(courseService.findById(resultSet.getLong("course_id"), rating.getStudent().getStudent_id()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rating;
    }


    public double getAverageRating(Long course_id){
        double rating = 0.0;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT AVG(rating) FROM rating WHERE course_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, course_id);
                ResultSet resultSet  = preparedStatement.executeQuery();
                if (resultSet.next()){
                    rating = resultSet.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rating;
    }
}
