package com.study.bookluck.repository;

import com.study.bookluck.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    List<User> findAll();
}
