package com.example.bookluck1.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 

import com.example.bookluck1.domain.User;
import com.example.bookluck1.dto.UserDto;
import com.example.bookluck1.repository.UserRepository;

@Service
public class UserService {

    // @Autowired
    // private final UserRepository userRepository;

    // public UserService(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }


    // public UserDto findItemById(int id) {
    //     User user = userRepository.findById(id); // itemRepository를 통해 id에 해당하는 item을 찾아서 반환

    //     return UserDto.builder()
    //             .id(1)
    //             .name("test1")
    //             .build(); // itemDto 반환
    // }

    public String getUser() {		
        return "service user!";	
    }

}
