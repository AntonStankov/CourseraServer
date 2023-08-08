package com.example.demo.service.teacher;


import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import org.jvnet.hk2.annotations.Service;

@Service
public interface TeacherService {



    TeacherTableManager teacherTableManager = new TeacherTableManager();
    public default Teacher save(Teacher teacher, User user){
        return teacherTableManager.insertTeacher(teacher, user);
    }
    public default Teacher findById(Long id){
        return teacherTableManager.getTeacherById(id);
    }

    public default Teacher findByUserId(Long userId){
        return teacherTableManager.getTeacherByUserId(userId);
    }

    public default Teacher updateTeacher(String name, Long id){
        return teacherTableManager.updateInfo(name, id);
    }
}
