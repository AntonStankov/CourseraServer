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
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

        Student student = new Student();
        student.setUser(user);
        student.setName(authRequest.getName());

        studentService.save(student, userService.findByEmail(user.getEmail()));
        return studentService.findById(student.getStudent_id(), user.getEmail());
    }

    @PostMapping("/register")
    public Object register(@RequestBody AuthRequest authRequest){
        if (authRequest.getRole().toString().equals("STUDENT")){
            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            user.setRole(UserRoleEnum.STUDENT);
            user.setTimeCreated(LocalDateTime.now());
            userService.save(user);

            Student student = new Student();
            student.setUser(user);
            student.setName(authRequest.getName());

            studentService.save(student, userService.findByEmail(user.getEmail()));
            return studentService.findById(student.getStudent_id(), user.getEmail());
        }
        else {
            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            user.setRole(UserRoleEnum.TEACHER);
            user.setTimeCreated(LocalDateTime.now());
            userService.save(user);

            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setName(authRequest.getName());

            teacherService.save(teacher, userService.findByEmail(user.getEmail()));
            return teacherService.findById(teacher.getTeacher_id());
        }
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

    @PostMapping("/changeEmail")
    public User changeEmail(@RequestBody User user, HttpServletRequest httpServletRequest){
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        return userService.changeEmil(user.getId(), user.getEmail());
    }


}
