package com.example.demo.service.tabCompletion;


import com.example.demo.entity.Enrollment;
import com.example.demo.entity.TabCompletion;
import org.springframework.stereotype.Repository;

@Repository
public interface TabCompletionService {

    TabCompletionTableManager completionTableManager = new TabCompletionTableManager();


    public default TabCompletion insertTabCompletion(Long studentId, Long tabId){
        return completionTableManager.insertTabCompletion(studentId, tabId);
    }
}
