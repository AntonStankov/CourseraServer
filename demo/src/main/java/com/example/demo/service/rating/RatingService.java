package com.example.demo.service.rating;

import com.example.demo.entity.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface RatingService {
    RatingTableManager ratingTableManager= new RatingTableManager();

    public default Rating insertRating(Long student_id, Long course_id, double rating){
        return ratingTableManager.insertRating(student_id, course_id, rating);
    }

    public default double getAverageRating(Long course_id){
        return ratingTableManager.getAverageRating(course_id);
    }
}
