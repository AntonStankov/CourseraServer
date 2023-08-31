package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Rating;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.enrollment.EnrollmentService;
import com.example.demo.service.rating.RatingService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/rating")
public class RatingController {

    private RatingService ratingService = new RatingService() {
        @Override
        public Rating insertRating(Long student_id, Long course_id, double rating) {
            return RatingService.super.insertRating(student_id, course_id, rating);
        }

        @Override
        public double getAverageRating(Long course_id) {
            return RatingService.super.getAverageRating(course_id);
        }

        @Override
        public Rating getRatingByCourseAndStudentId(Long student_id, Long course_id) {
            return RatingService.super.getRatingByCourseAndStudentId(student_id, course_id);
        }

        @Override
        public void updateRatingCount(Long student_id, Long course_id) {
            RatingService.super.updateRatingCount(student_id, course_id);
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

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/rateCourse")
    public Rating rateCourse(@RequestParam Long course_id, @RequestParam double rating, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().equals(UserRoleEnum.TEACHER)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a student!");
        Student student = studentService.findStudentByUserId(user.getId());
        if (enrollmentService.findEnrollmentByStudnentAndCourseIds(course_id, student.getStudent_id()) == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not signed for this course!");
        if (ratingService.getRatingByCourseAndStudentId(student.getStudent_id(), course_id).getRating_id() != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already rated this course!");
        return ratingService.insertRating(student.getStudent_id(), course_id, rating);
    }
}
