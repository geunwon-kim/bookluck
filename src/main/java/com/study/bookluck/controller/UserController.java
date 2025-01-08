package com.study.bookluck.controller;

import com.study.bookluck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
 
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
