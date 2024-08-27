package com.example.bookluck1.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String name;

    @Builder
    public UserDto(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
