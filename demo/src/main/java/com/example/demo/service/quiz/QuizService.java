package com.example.demo.service.quiz;


import com.example.demo.entity.Answers;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Question;
import com.example.demo.entity.Quiz;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizService {



    QuizTableManager quizTableManager = new QuizTableManager();
    public default Quiz save(String quizName, Long courseId){
        return quizTableManager.insertQuiz(quizName, courseId);
    }

    public default Quiz getQuizByCourseId(Long courseId){
        return quizTableManager.getQuizByCourseId(courseId);
    }

    public default Quiz getQuizById(Long quizId){
        return quizTableManager.getQuizById(quizId);
    }

    public default Answers insertAnswer(Long questionId, String answer){
        return quizTableManager.insertAnswer(questionId, answer);
    }

    public default Question insertQuestion(Long quizId, String question, String rightAnswer){
        return quizTableManager.insertQuestion(quizId, question, rightAnswer);
    }

    public default Question getQuestionById(Long questionId){
        return quizTableManager.getQuestionById(questionId);
    }

    public default boolean checkQuestionInQuiz(Long quizId, Long questionId){
        return quizTableManager.checkQuestionInQuiz(quizId, questionId);
    }

    public default List<Question> getQuestionsByQuizId(Long quizId, boolean teacher){
        return quizTableManager.getQuestionsByQuizId(quizId, teacher);
    }

}
