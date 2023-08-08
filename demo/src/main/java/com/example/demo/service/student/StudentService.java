package com.example.demo.service.student;

import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public interface StudentService {

    StudentTableManager studentTableManager = new StudentTableManager();

    default Student save(Student student, User user){
        return studentTableManager.insertStudent(student, user);
    }

    default Student findById(Long id){
        return studentTableManager.getStudentById(id);
    }

    default Student findStudentByUserId(Long userId){
        return studentTableManager.getStudentByUserId(userId);
    }

    default Student changeName(String name, Long id){
        return studentTableManager.updateStudentName(name, id);
    }
}
