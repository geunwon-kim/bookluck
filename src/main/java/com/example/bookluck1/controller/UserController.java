package com.example.bookluck1.controller;

import com.example.bookluck1.domain.User;
import com.example.bookluck1.dto.UserDto;
import com.example.bookluck1.service.UserService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/users")
public class UserController {

    // @GetMapping("/test")
    // public String test() {
    //     return new String("test");
    // }

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("searchUser")
    public ResponseEntity<?> searchUser(@RequestBody User user) {
        //TODO: process POST request

        //저장로직 추가

        System.out.print("id: "+user.getId());
        
        // return entity;
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PostMapping("test")
    public ResponseEntity<?> test(@RequestBody User user) {
        //TODO: process POST request

        //저장로직 추가
        System.out.print("id: "+user.getId());
        user.setId(1111);
        user.setName(user.getName()+" response");
        
        // return entity;
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }
    

    @GetMapping("/test")
    public User test() {
        User user = new User(1,"test1");
        return user;
    }
    

}
