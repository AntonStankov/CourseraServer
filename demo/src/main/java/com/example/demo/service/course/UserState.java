package com.example.demo.service.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.index.qual.SearchIndexBottom;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserState {
    private boolean enrolled;
    private boolean completed;
}
