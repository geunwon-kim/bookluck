package com.example.bookluck1.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int id; // User id
    private String name; // User name

    @Builder
    public User(int id, String name) {
        this.id = id;
        this.name = name;        
    }
}