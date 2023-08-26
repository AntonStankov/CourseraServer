package com.example.demo.service.enrollment;


import com.example.demo.controller.PaginationResponse;
import com.example.demo.config.DataSource;
import com.example.demo.entity.*;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.tab.TabsService;
import com.example.demo.service.user.UserService;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Repository
public class EnrollmentTableManager {

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
        public List<Tab> findTabsByCourseId(Long courseId, Long studentId) {
            return TabsService.super.findTabsByCourseId(courseId, studentId);
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


    public Enrollment insertEnrollment(Long studentId, Long courseId) {
        Long generatedEnrollmentId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO enrollment (student_id, course_id, completed) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setLong(1, studentId);
                preparedStatement.setLong(2, courseId);
                preparedStatement.setBoolean(3, false);
//                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.MAX));
                int affectedRows = preparedStatement.executeUpdate();

                int credit = courseService.findById(courseId, studentId).getCredit();
                studentService.addCredit(studentId, credit);
                courseService.addStudentsCount(courseId);
                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedEnrollmentId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getEnrollmentById(generatedEnrollmentId);
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM enrollment e " +
                        "JOIN courses c ON e.course_id = c.courseId"
                    ;

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setEnrollment_id(resultSet.getLong("enrollment_id"));
                    enrollment.setCourse(resultSet.getObject("course_id", Course.class));
                    enrollment.setStudent(resultSet.getObject("student_id", Student.class));
                    enrollment.setCompletion_date(resultSet.getTimestamp("completion_date").toLocalDateTime());
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }


    public Enrollment getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM enrollment e " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN courses c ON e.course_id = c.courseId " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE e.enrollment_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, enrollmentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        enrollment = new Enrollment();
                        enrollment.setEnrollment_id(resultSet.getLong("enrollment_id"));
                        enrollment.setCompleted(resultSet.getBoolean("completed"));
//                        enrollment.setCompletion_date(resultSet.getTimestamp("completion_date").toLocalDateTime());

                        Student student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setName(resultSet.getString("name"));
                        student.setCredit(resultSet.getLong("credit"));
                        student.setUser(userService.findUserByStudentId(resultSet.getLong("student_id")));
                        enrollment.setStudent(student);
                        Course course = new Course();
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setDescription(resultSet.getString("description"));
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setTabs(tabsService.findTabsByCourseId(resultSet.getLong("courseId"), null));
                        Teacher teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        teacher.setUser(userService.findUserByTeacherId(resultSet.getLong("teacher_id")));
                        course.setTeacher(teacher);
                        enrollment.setCourse(course);


                        enrollment.setCourse(course);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return enrollment;
    }

    public Enrollment updateEnrollment(Long courseId, Long studentId) throws ResponseStatusException {
        Long enrollmentId = findEnrollmentByStudentAndCourseIds(courseId, studentId);
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        if (enrollment.getCompleted().equals(true)) return null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE enrollment SET completed = ?, completion_date = ? WHERE enrollment_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBoolean(1, true);
                preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setLong(3, enrollmentId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Enrollment enrollment1 = getEnrollmentById(enrollmentId);
        enrollment1.setCompletion_date(LocalDateTime.now());
        return enrollment1;
    }

    public Long findEnrollmentByStudentAndCourseIds(Long courseId, Long studentId){
        Enrollment enrollment = new Enrollment(null, null, null, null, null);
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM enrollment e " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN courses c ON e.course_id = c.courseId " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE e.course_id = ? AND e.student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                preparedStatement.setLong(2, studentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        enrollment = new Enrollment();
                        enrollment.setEnrollment_id(resultSet.getLong("enrollment_id"));
                        enrollment.setCompleted(resultSet.getBoolean("completed"));
                        Student student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setName(resultSet.getString("name"));
                        student.setCredit(resultSet.getLong("credit"));
                        enrollment.setStudent(student);
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setTabs(tabsService.findTabsByCourseId(resultSet.getLong("courseId"), studentId));

                        Teacher teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        course.setTeacher(teacher);
                        enrollment.setCourse(course);


                        enrollment.setCourse(course);
                    }
                }
            }
        } catch (SQLException e) {
            //
        }
        return enrollment.getEnrollment_id();
    }

    public void deleteEnrollment(long enrollmentId) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "DELETE FROM enrollment WHERE enrollment_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, enrollmentId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
