package com.study.bookluck.service;

import com.study.bookluck.entity.User;
import com.study.bookluck.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor

public class UserService {
    private final UserMapper userMapper;
 
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

}
