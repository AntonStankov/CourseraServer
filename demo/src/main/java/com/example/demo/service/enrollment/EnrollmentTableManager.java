package com.example.demo.service.enrollment;


import com.example.demo.controller.PaginationResponse;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.config.DataSource;
import com.example.demo.entity.*;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.student.StudentService;
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
        public PaginationResponse findUncompleteCourses(Long userId, int page, int pageSize) {
            return CourseService.super.findUncompleteCourses(userId, page, pageSize);
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
        if (findEnrollmentByStudentAndCourseIds(courseId, studentId) != null) throw new RuntimeException("Already signed for this course!");
        Long generatedEnrollmentId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO enrollment (student_id, course_id, completed) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setLong(1, studentId);
                preparedStatement.setLong(2, courseId);
                preparedStatement.setBoolean(3, false);
//                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.MAX));
                int affectedRows = preparedStatement.executeUpdate();

                int credit = courseService.findById(courseId).getCredit();
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
                        enrollment.setStudent(student);
                        Course course = new Course();
                        course.setStudentsCount(resultSet.getLong("students_count"));
                        course.setDuration(resultSet.getLong("duration"));
                        course.setPicturePath(resultSet.getString("picture_path"));
                        course.setDescription(resultSet.getString("description"));
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
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
            // Handle the exception
        }
        return enrollment;
    }

    public Enrollment updateEnrollment(Long courseId, Long studentId) throws ResponseStatusException {
        Long enrollmentId = findEnrollmentByStudentAndCourseIds(courseId, studentId);
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        if (enrollment.getCompleted().equals(true)) throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Already completed this course!");
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

    private Long findEnrollmentByStudentAndCourseIds(Long courseId, Long studentId){
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
