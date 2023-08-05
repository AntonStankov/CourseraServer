package com.example.demo.service.enrollment;


import com.example.demo.entity.Enrollment;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentService {

    EnrollmentTableManager enrollmentTableManager = new EnrollmentTableManager();
    public default Enrollment save( Long studentId,
                                    Long courseId){
        return enrollmentTableManager.insertEnrollment(studentId, courseId);
    }
}
