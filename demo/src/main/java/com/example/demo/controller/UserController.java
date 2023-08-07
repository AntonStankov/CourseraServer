package com.example.demo.controller;

import com.example.demo.enums.UserRoleEnum;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.jwt.BlackListService;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {


    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher save(Teacher teacher, User user) {
            return TeacherService.super.save(teacher, user);
        }
    };
    @Autowired
    private JwtTokenService jwtTokenService;


    private UserService userService = new UserService() {
        @Override
        public User save(User user) {
            return UserService.super.save(user);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }

        @Override
        public User updateUser(User user, Long id) {
            return UserService.super.updateUser(user, id);
        }

        @Override
        public User findUserById(Long id) {
            return UserService.super.findUserById(id);
        }

        @Override
        public void deleteUser(Long id) {
            UserService.super.deleteUser(id);
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

    @Autowired
    private BlackListService blackListService;







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
        return teacherService.findById(teacher.getTeacher_id());
        //Should write validator if email is already registered

    }
//
//
//
//
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {

        User user = userService.findByEmail(authRequest.getEmail());
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())){
            String token = jwtTokenService.generateToken(user.getEmail());
            String refreshToken = jwtTokenService.generateRefreshToken(authRequest.getEmail());
            return new AuthResponse("", token, refreshToken, user);
        }
        else throw new RuntimeException("Invalid email or password!");
    }

    @GetMapping("/refresh/v1")
    public AuthResponse refresh(HttpServletRequest httpServletRequest){
        String refreshToken = jwtTokenService.getTokenFromRequest(httpServletRequest);
        String email = jwtTokenService.getEmailFromToken(refreshToken);
        if (userService.findByEmail(email) == null) throw new RuntimeException("No user with this email " + email + " ! Someone is hacker!");
        String newToken = jwtTokenService.generateToken(email);
        return new AuthResponse("", newToken, refreshToken, userService.findByEmail(email));
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest){
        String token = jwtTokenService.getTokenFromRequest(httpServletRequest);
        blackListService.addTokenToBlacklist(token);
        return "Logged out!";
    }

    @GetMapping("/test/hello")
    public String hello(){
        return "hello user";
    }


    //Other crud operations
    @PostMapping("/update")
    public User updateUser(@RequestBody User user, HttpServletRequest httpServletRequest){
        String email = jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest));
        User myUser = userService.findByEmail(email);
        return userService.updateUser(user, myUser.getId());
    }

    @PostMapping("/updateName/teacher")
    public Teacher updateNames(@RequestBody Teacher teacher){
        return teacherService.updateTeacher(teacher);
    }


}
