package com.example.demo.controller;

import com.example.demo.UserRoleEnum;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtTokenService jwtTokenService;


    private UserService userService = new UserService() {
        @Override
        public User save(User user) {
            return UserService.super.save(user);
        }
    };

    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher save(Teacher teacher, User user) {
            return TeacherService.super.save(teacher, user);
        }
    };

    private StudentService studentService = new StudentService() {
        @Override
        public Student save(Student student, User user) {
            return StudentService.super.save(student, user);
        }
    };



    @Autowired
    private JwtTokenService jwtTokenUtil;







    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController() {
    }

    @PostMapping("/register/student")
    public Student registerStudent(@RequestBody AuthRequest authRequest){
        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(UserRoleEnum.STUDENT);
        user.setTimeCreated(LocalDateTime.now());
        userService.save(user);

//        userRepository.save(user);
        Student student = new Student();
        student.setUser(user);
        student.setFirstName(authRequest.getFirstName());
        student.setLastName(authRequest.getLastName());
//        return teacherRepository.save(teacher);

        studentService.save(student, userService.findByEmail(user.getEmail()));
        return studentService.findById(student.getStudent_id(), user.getEmail());
    }

    @PostMapping("/register/teacher")
    public Teacher registerTeacher(@RequestBody AuthRequest authRequest){
        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(UserRoleEnum.TEACHER);
        user.setTimeCreated(LocalDateTime.now());
        userService.save(user);

//        userRepository.save(user);
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setName(authRequest.getName());
//        return teacherRepository.save(teacher);

        teacherService.save(teacher, userService.findByEmail(user.getEmail()));
        return teacherService.findById(teacher.getTeacher_id(), user.getEmail());
        //Should write validator if it has same email

    }
//
//
//
//
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {

        User user = userService.findByEmail(authRequest.getEmail());
        String token = jwtTokenService.generateToken(user.getEmail());



        return new AuthResponse("", token, user);


    }

    @GetMapping("/test/hello")
    public String hello(){
        return "hello user";
    }


}
