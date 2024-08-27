package com.example.bookluck1.repository;

import com.example.bookluck1.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    // @Override
    // public User findById(Long id) {
    //     return store.get(id); // id에 해당하는 item을 찾아서 반환
    // }


    void save(User user);
    User findById(int id);

    
    // List<User> findAll();
    // void updateById(Long id, User book);
    // void deleteById(Long id);

}
