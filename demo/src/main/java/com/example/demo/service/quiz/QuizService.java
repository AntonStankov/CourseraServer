package com.example.demo.service.quiz;


import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Question;
import com.example.demo.entity.Quiz;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizService {

    QuizTableManager QUIZ_TABLE_MANAGER = new QuizTableManager();
    public default Quiz save(String quizName){
        return QUIZ_TABLE_MANAGER.insertQuiz(quizName);
    }

}
