package com.example.demo.controller;

import com.example.demo.enums.TabContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateTabRequest {

    private String tabName;
    private TabContentType contentType;
    private String content;
    private Long courseId;
    private MultipartFile file;
}
