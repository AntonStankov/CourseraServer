package com.example.demo.service.tab;


import com.example.demo.config.DataSource;
import com.example.demo.controller.PaginationResponse;
import com.example.demo.entity.Course;
import com.example.demo.entity.Tab;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.enums.TabContentType;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.course.UserState;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TabsTableManager {

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

    private final DataSource datasource = new DataSource();

    public Tab insertTab(Tab tab, Long courseId) {
        Long generatedTabId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO tabs (contentType, content, course_id) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, tab.getContentType().toString());
                preparedStatement.setString(2, tab.getContent());
                preparedStatement.setLong(3, courseId);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedTabId = generatedKeys.getLong(1);
                        tab.setTab_id(generatedTabId);
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findTabById(generatedTabId);
    }

    public Tab findTabById(Long id) {
        Tab tab = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT a.*, c.* FROM tabs a JOIN courses c ON a.course_id = c.courseId WHERE a.tab_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        tab = new Tab();
                        tab.setTab_id(resultSet.getLong("tab_id"));
                        tab.setContent(resultSet.getString("content"));
                        tab.setContentType(TabContentType.valueOf(resultSet.getString("contentType")));
//                        tab.setCourse(courseService.findById(resultSet.getLong("course_id")));
                    }
                }
            }
        } catch (SQLException e) {
            //
        }
        return tab;
    }

    public List<Tab> findTabsByCourseId(Long courseId) {
        List<Tab> tabs = new ArrayList<>();
        Tab tab = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT a.*, c.* FROM tabs a JOIN courses c ON a.course_id = c.courseId WHERE a.course_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        tab = new Tab();
                        tab.setTab_id(resultSet.getLong("tab_id"));
                        tab.setContent(resultSet.getString("content"));
                        tab.setContentType(TabContentType.valueOf(resultSet.getString("contentType")));
//                        tab.setCourse(courseService.findById(resultSet.getLong("course_id")));
                        tabs.add(tab);
                    }
                }
            }
        } catch (SQLException e) {
            //
        }
        return tabs;
    }
}
