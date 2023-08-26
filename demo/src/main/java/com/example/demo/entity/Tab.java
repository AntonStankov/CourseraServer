package com.example.demo.entity;

import com.example.demo.enums.TabContentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tabs")
public class Tab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tab_id;

    @Column
    private String tabName;

    @Column
    private TabContentType contentType;

    @Column
    private String content;

    @ManyToOne
    private Course course;

    @Column
    private boolean completed;
}
