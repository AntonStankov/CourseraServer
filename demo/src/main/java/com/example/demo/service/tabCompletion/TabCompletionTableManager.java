package com.example.demo.service.tabCompletion;


import com.example.demo.config.DataSource;
import com.example.demo.controller.PaginationResponse;
import com.example.demo.entity.*;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.tab.TabsService;
import com.example.demo.service.user.UserService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Repository
public class TabCompletionTableManager {


    private DataSource datasource = new DataSource();

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


    private TabsService tabsService = new TabsService() {
        @Override
        public Tab insertTab(Tab tab, Long courseId) {
            return TabsService.super.insertTab(tab, courseId);
        }

        @Override
        public List<Tab> findTabsByCourseId(Long courseId) {
            return TabsService.super.findTabsByCourseId(courseId);
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
    };


    public TabCompletion insertTabCompletion(Long studentId, Long tabId) {
        Long generatedCompletionId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO tabCompletion (student_id, tab_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setLong(1, studentId);
                preparedStatement.setLong(2, tabId);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedCompletionId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getTabCompletionById(generatedCompletionId);
    }


    public TabCompletion getTabCompletionById(Long tabCompletionId) {
        TabCompletion tabCompletion = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM tabCompletion e " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "WHERE e.completion_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, tabCompletionId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        tabCompletion = new TabCompletion();
                        tabCompletion.setCompletion_id(resultSet.getLong("completion_id"));
//                        enrollment.setCompletion_date(resultSet.getTimestamp("completion_date").toLocalDateTime());

                        Student student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setName(resultSet.getString("name"));
                        student.setCredit(resultSet.getLong("credit"));
                        student.setUser(userService.findUserByStudentId(resultSet.getLong("student_id")));
                        tabCompletion.setStudent(student);
                        tabCompletion.setTab(tabsService.findById(resultSet.getLong("tab_id"), student.getStudent_id()));
                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return tabCompletion;
    }


}
