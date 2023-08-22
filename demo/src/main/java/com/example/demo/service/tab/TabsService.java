package com.example.demo.service.tab;

import com.example.demo.entity.Tab;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TabsService {

    TabsTableManager tabsTableManager = new TabsTableManager();

    public default Tab insertTab(Tab tab, Long courseId){
        return tabsTableManager.insertTab(tab, courseId);
    }

    public default List<Tab> findTabsByCourseId(Long courseId){
        return tabsTableManager.findTabsByCourseId(courseId);
    }

    public default Tab editTab(Tab tab, Long id){
        return tabsTableManager.updateTab(tab, id);
    }

    public default Tab findById(Long id){
        return tabsTableManager.findTabById(id);
    }

}
