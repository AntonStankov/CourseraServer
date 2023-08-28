package com.example.demo.service.course;


import com.example.demo.config.DataSource;
import com.example.demo.controller.PaginationResponse;
import com.example.demo.entity.*;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.tab.TabsService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import org.aspectj.weaver.ast.Literal;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CoursesTableManager {

    private TabsService tabsService = new TabsService() {
        @Override
        public Tab insertTab(Tab tab, Long courseId) {
            return TabsService.super.insertTab(tab, courseId);
        }

        @Override
        public List<Tab> findTabsByCourseId(Long courseId, Long studentId) {
            return TabsService.super.findTabsByCourseId(courseId, studentId);
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

    private final DataSource datasource = new DataSource();
    public Course insertCourse(Course course, Teacher teacher) {
        Long generatedCourseId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO courses (courseName, description, duration, credit, time_created) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, course.getCourseName());
                preparedStatement.setString(2, course.getDescription());
                preparedStatement.setLong(3, course.getDuration());
                preparedStatement.setInt(4, course.getCredit());
                preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedCourseId = generatedKeys.getLong(1);
                        course.setCourseId(generatedCourseId);
                    }
                }
            }

            // Associate the User with the Teacher
            associateCourseWithTeacher(connection, course.getCourseId(), teacher.getTeacher_id());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getCourseById(generatedCourseId, null);
    }

    private void associateCourseWithTeacher(Connection connection, Long courseId, Long teacherId) {
        String sql = "UPDATE courses SET teacher_id = ? WHERE courseId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, teacherId);
            preparedStatement.setLong(2, courseId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Course getCourseById(Long courseId, Long studentId) {
        Course course = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT c.*, t.*, u.* FROM courses c " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "JOIN app_users u ON t.user_id = u.id " +
                    "WHERE c.courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());


                        // Create a Teacher object and set its attributes
                        Teacher teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        teacher.setUser(userService.findByEmail(resultSet.getString("email")));
                        // Set other teacher attributes

                        // Set the Teacher object for the Course
                        course.setTeacher(teacher);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return course;
    }

    public Course getCourseByIdWithTabs(Long courseId, Long studentId) {
        Course course = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT c.*, t.*, u.* FROM courses c " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "JOIN app_users u ON t.user_id = u.id " +
                    "WHERE c.courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setTabs(tabsService.findTabsByCourseId(resultSet.getLong("courseId"), studentId));
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());


                        // Create a Teacher object and set its attributes
                        Teacher teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        teacher.setUser(userService.findByEmail(resultSet.getString("email")));
                        // Set other teacher attributes

                        // Set the Teacher object for the Course
                        course.setTeacher(teacher);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return course;
    }

    public UserState checkEnrollment(Long courseId, Long studentId) {
        boolean enrolled = false;
        boolean completed = false;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT CASE " +
                    "    WHEN EXISTS (SELECT * FROM enrollment e WHERE e.course_id = ? AND e.student_id = ?) " +
                    "    THEN TRUE " +
                    "    ELSE FALSE " +
                    "END AS enrolled;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setLong(1, courseId);
                preparedStatement.setLong(2, studentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                       enrolled = resultSet.getBoolean("enrolled");
                    }
                }
            }
            if (enrolled){
                String query = "SELECT * FROM enrollment e WHERE e.course_id = ? AND e.student_id = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setLong(1, courseId);
                    preparedStatement.setLong(2, studentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            completed = resultSet.getBoolean("completed");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // Handle the exception
        }
        return new UserState(enrolled, completed);
    }

    public void updateCourse(Course course) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE courses SET courseName = ?, description = ? WHERE courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, course.getCourseName());
                preparedStatement.setString(2, course.getDescription());
                preparedStatement.setLong(3, course.getCourseId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(Long courseId) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "DELETE FROM courses WHERE courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PaginationResponse findUncompletedCourses(Long studentId, int page, int pageSize) {
        Long totalSavings = 0L;
        List<Course> courses = new ArrayList<Course>();

        try (Connection connection = datasource.createConnection()) {
            String countSql = "SELECT COUNT(*) AS savings_count FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE NOT EXISTS " +
                    "(SELECT * FROM enrollment e " +
                    "WHERE e.student_id = ? AND e.course_id = c.courseId)";

            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                countStatement.setLong(1, studentId);
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        totalSavings = countResultSet.getLong("savings_count");
                    }
                }
            }

            String dataSql = "SELECT * FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE NOT EXISTS " +
                    "(SELECT * FROM enrollment e " +
                    "WHERE e.student_id = ? AND e.course_id = c.courseId) " +
                    "ORDER BY students_count DESC " +
                    "LIMIT ? OFFSET ?";

            try (PreparedStatement dataStatement = connection.prepareStatement(dataSql)) {
                dataStatement.setLong(1, studentId);
                dataStatement.setInt(2, pageSize);
                dataStatement.setInt(3, (page - 1) * pageSize);

                try (ResultSet resultSet = dataStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setTeacher(teacherService.findById(resultSet.getLong("teacher_id")));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());

                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PaginationResponse(totalSavings, courses);
    }


    public PaginationResponse findCompletedCourses(Long studentId, int page, int pageSize, Boolean completed) {

        Long totalSavings = 0L;
        List<Course> courses = new ArrayList<>();

        try (Connection connection = datasource.createConnection()) {

            String countSql = "SELECT COUNT(*) AS savings_count FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE EXISTS " +
                    "(SELECT * FROM enrollment e " +
                    "WHERE e.student_id = ? AND e.course_id = c.courseId AND e.completed = ?)";

            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                countStatement.setLong(1, studentId);
                countStatement.setBoolean(2, completed);
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        totalSavings = countResultSet.getLong("savings_count");
                    }
                }
            }
            String sql = "SELECT * FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE EXISTS " +
                    "(SELECT * FROM enrollment e " +
                    "WHERE e.student_id = ? AND e.course_id = c.courseId AND e.completed = ?) ";
//                    "ORDER BY students_count DESC " +
//                    "LIMIT ? OFFSET ?";
            if (completed) {
                sql += "ORDER BY (SELECT completion_date FROM enrollment e " +
                        "WHERE e.student_id = ? AND e.course_id = c.courseId AND e.completed = ?) DESC, " +
                        "students_count DESC ";
            } else {
                sql += "ORDER BY students_count DESC ";
            }

            sql += "LIMIT ? OFFSET ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, studentId);
                preparedStatement.setBoolean(2, completed);

                if (completed) {
                    preparedStatement.setLong(3, studentId);
                    preparedStatement.setBoolean(4, completed);
                    preparedStatement.setInt(5, pageSize);
                    preparedStatement.setInt(6, (page - 1) * pageSize);
                } else {
                    preparedStatement.setInt(3, pageSize);
                    preparedStatement.setInt(4, (page - 1) * pageSize);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setTeacher(teacherService.findById(resultSet.getLong("teacher_id")));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());


                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PaginationResponse(totalSavings, courses);
    }

    public PaginationResponse findAllCourses(Long userId, int page, int pageSize) {
        Long totalSavings = 0L;
        List<Course> courses = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {

            String countSql = "SELECT COUNT(*) AS savings_count FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id ";

            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        totalSavings = countResultSet.getLong("savings_count");
                    }
                }
            }
            String sql = "SELECT * FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "ORDER BY students_count DESC " +
                    "LIMIT ? OFFSET ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, pageSize);
                preparedStatement.setInt(2, (page - 1) * pageSize);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Long studentId = 0L;
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setTeacher(teacherService.findById(resultSet.getLong("teacher_id")));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        if (studentService.findStudentByUserId(userId) != null) studentId = studentService.findStudentByUserId(userId).getStudent_id();
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());


                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PaginationResponse(totalSavings, courses);
    }

    public void setImagePath(Long courseId, String path) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE courses SET picture_path = ? WHERE courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, path);
                preparedStatement.setLong(2, courseId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStudentsCount(Long courseId) {
        Long count = getCourseById(courseId, null).getStudentsCount();
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE courses SET students_count = ? WHERE courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, count + 1);
                preparedStatement.setLong(2, courseId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public PaginationResponse findTeachersCourses(Long teacherId, int page, int pageSize) {
        Long totalSavings = 0L;
        List<Course> courses = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {

            String countSql = "SELECT COUNT(*) AS savings_count FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE c.teacher_id = ?";

            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                countStatement.setLong(1, teacherId);
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        totalSavings = countResultSet.getLong("savings_count");
                    }
                }
            }
            String sql = "SELECT * FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE c.teacher_id = ? " +
                    "ORDER BY students_count DESC " +
                    "LIMIT ? OFFSET ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, teacherId);
                preparedStatement.setInt(2, pageSize);
                preparedStatement.setInt(3, (page - 1) * pageSize);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setTeacher(teacherService.findById(resultSet.getLong("teacher_id")));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());



                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PaginationResponse(totalSavings, courses);
    }


    public PaginationResponse searchAllCoursesByName(int page, int pageSize, String name) {
        Long totalSavings = 0L;
        List<Course> courses = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {

            String countSql = "SELECT COUNT(*) AS savings_count FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE c.courseName LIKE ?";

            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                countStatement.setString(1, "%" + name + "%");
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        totalSavings = countResultSet.getLong("savings_count");
                    }
                }
            }
            String sql = "SELECT * FROM courses c " +
                    "JOIN teachers t ON t.teacher_id = c.teacher_id " +
                    "WHERE c.courseName LIKE ? " +
                    "ORDER BY students_count DESC " +
                    "LIMIT ? OFFSET ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "%" + name + "%");
                preparedStatement.setInt(2, pageSize);
                preparedStatement.setInt(3, (page - 1) * pageSize);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));
                        course.setDescription(resultSet.getString("description"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setTeacher(teacherService.findById(resultSet.getLong("teacher_id")));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setTime_created(resultSet.getTimestamp("time_created").toLocalDateTime());


                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PaginationResponse(totalSavings, courses);
    }
}
