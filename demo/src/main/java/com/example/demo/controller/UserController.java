package com.example.demo.controller;

import com.example.demo.entity.UserSecrets;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.jwt.BlackListService;
import com.example.demo.jwt.JwtTokenService;
import com.example.demo.service.secrets.SecretsService;
import com.example.demo.service.student.StudentService;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:5173")
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
        public User save(User user, String password) {
            return UserService.super.save(user, password);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }

//        @Override
//        public User updateUser(User user, Long id) {
//            return UserService.super.updateUser(user, id);
//        }

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
    };

    private SecretsService secretsService = new SecretsService() {
        @Override
        public UserSecrets save(UserSecrets userSecrets) {
            return SecretsService.super.save(userSecrets);
        }

        @Override
        public UserSecrets findById(Long id) {
            return SecretsService.super.findById(id);
        }

        @Override
        public void changePassword(String password, Long userId) {
            SecretsService.super.changePassword(password, userId);
        }
    };



    @Autowired
    private JwtTokenService jwtTokenUtil;

    @Autowired
    private BlackListService blackListService;







    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController() {
    }

    @PostMapping("/register")
    public Object register(@RequestBody AuthRequest authRequest){
        if (userService.findByEmail(authRequest.getEmail()) != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST_400)
                .body("Email already exists!");
        if (authRequest.getRole().toString().equals("STUDENT")){
            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setRole(UserRoleEnum.STUDENT);
            user.setTimeCreated(LocalDateTime.now());
            userService.save(user, authRequest.getPassword());

            Student student = new Student();
            student.setUser(user);
            student.setName(authRequest.getName());

            studentService.save(student, userService.findByEmail(user.getEmail()));
            return studentService.findById(student.getStudent_id());
        }
        else {
            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setRole(UserRoleEnum.TEACHER);
            user.setTimeCreated(LocalDateTime.now());
            userService.save(user, authRequest.getPassword());

            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setName(authRequest.getName());

            teacherService.save(teacher, userService.findByEmail(user.getEmail()));
            return teacherService.findById(teacher.getTeacher_id());
        }
    }


    @PostMapping("/login")
    public Object login(@RequestBody AuthRequest authRequest) {

        User user = userService.findByEmail(authRequest.getEmail());
        UserSecrets userSecrets = secretsService.findById(user.getId());
        if (passwordEncoder.matches(authRequest.getPassword(), userSecrets.getPassword())){
            String token = jwtTokenService.generateToken(user.getEmail());
            String refreshToken = jwtTokenService.generateRefreshToken(authRequest.getEmail());
            return new AuthResponse("", token, refreshToken, user);
        }
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST_400).body("Invalid email or password!");
    }

    @GetMapping("/refresh/v1")
    public AuthResponse refresh(HttpServletRequest httpServletRequest){
        String refreshToken = jwtTokenService.getTokenFromRequest(httpServletRequest);
        String email = jwtTokenService.getEmailFromToken(refreshToken);
        if (userService.findByEmail(email) == null) throw new RuntimeException("No user with this email " + email + " ! Someone is hacker!");
        String newToken = jwtTokenService.generateToken(email);
        return new AuthResponse("", newToken, refreshToken, userService.findByEmail(email));
    }

    @CrossOrigin(origins = "http://localhost:5173")
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

    @PostMapping("/updateName")
    public Object updateNames(@RequestBody AuthRequest authRequest, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("TEACHER")){
            Teacher teacher = teacherService.findByUserId(user.getId());
            return teacherService.updateTeacher(authRequest.getName(), teacher.getTeacher_id());
        }
        else if(user.getRole().toString().equals("STUDENT")){
            Student student = studentService.findStudentByUserId(user.getId());
            return studentService.changeName(authRequest.getName(), student.getStudent_id());
        }
        return null;

    }

    @PostMapping("/changeEmail")
    public User changeEmail(@RequestBody AuthRequest authRequest, HttpServletRequest httpServletRequest){
        User myuser = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");
        return userService.changeEmil(myuser.getId(), authRequest.getEmail());
    }

    @PostMapping("/changePassword")
    public void changePassword(@RequestBody ChangePasswordRequest request, HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest)));
        UserSecrets userSecrets = secretsService.findById(user.getId());
        if (passwordEncoder.matches(request.getCurrentPassword(), userSecrets.getPassword())){
            secretsService.changePassword(request.getPassword(), userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenService.getTokenFromRequest(httpServletRequest))).getId());
        }
        else throw new RuntimeException("Wrong password!");
    }

    @GetMapping("/getProfile")
    public Object getProfile(HttpServletRequest httpServletRequest){
        User user = userService.findByEmail(jwtTokenService.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));
        if (user.getRole().toString().equals("STUDENT")){
            Student student = studentService.findStudentByUserId(user.getId());
            return new ProfileResponse(student.getName(), user.getEmail(), user.getRole(), user.getTimeCreated(), student.getCredit());
        }
        else if (user.getRole().toString().equals("TEACHER")){
            Teacher teacher = teacherService.findByUserId(user.getId());
            return new ProfileResponse(teacher.getName(), user.getEmail(), user.getRole(), user.getTimeCreated());
        }
        else throw new RuntimeException("No user with this ID!");
    }

}
