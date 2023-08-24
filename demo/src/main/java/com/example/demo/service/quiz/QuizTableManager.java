package com.example.demo.service.quiz;


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
public class QuizTableManager {


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


    public Quiz insertQuiz(String quizName) {
        Long generatedQuizId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO quiz (quizName) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, quizName);
//                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.MAX));
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedQuizId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getQuizById(generatedQuizId);
    }

    public Quiz getQuizById(Long quizId) {
        Quiz quiz = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM quiz e " +
                    "WHERE e.quiz_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, quizId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        quiz = new Quiz();
                        quiz.setQuizName(resultSet.getString("quizName"));
                        quiz.setQuiz_id(resultSet.getLong("quiz_id"));
                        quiz.setQuestions(getQuestionsByQuizId(resultSet.getLong("quiz_id")));

                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return quiz;
    }

    public List<Question> getQuestionsByQuizId(Long quizId) {
        List<Question> questions = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM question e " +
                    "WHERE e.quiz_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, quizId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Question question = new Question();
                        question.setQuestion_id(resultSet.getLong("question_id"));
                        question.setQuestion(resultSet.getString("question"));
                        question.setRightAnswer(resultSet.getString("rightAnswer"));
                        question.setAnswers(getAnswersByQuestionId(resultSet.getLong("question_id")));

                        questions.add(question);

                    }
                }
            }
        } catch (SQLException e) {
            //
        }
        return questions;
    }

    public List<Answers> getAnswersByQuestionId(Long questionId) {
        List<Answers> answers = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM answers e " +
                    "WHERE e.question_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, questionId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Answers answer = new Answers();
                        answer.setAnswer_id(resultSet.getLong("answer_id"));
                        answer.setAnswer(resultSet.getString("answer"));

                        answers.add(answer);

                    }
                }
            }
        } catch (SQLException e) {
            //
        }
        return answers;
    }


    public Question getQuestionById(Long questionId) {
        Question question = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM answers e " +
                    "WHERE e.question_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, questionId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        question = new Question();
                        question.setQuiz(getQuizById(resultSet.getLong("quiz_id")));
                        question.setQuestion_id(resultSet.getLong("question_id"));
                        question.setQuestion(resultSet.getString("question"));
                        question.setRightAnswer(resultSet.getString("rightAnswer"));
                    }
                }
            }
        } catch (SQLException e) {
            //
        }
        return question;
    }



}
