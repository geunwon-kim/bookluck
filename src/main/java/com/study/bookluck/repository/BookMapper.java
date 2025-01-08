package com.study.bookluck.repository;

import com.study.bookluck.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
@Mapper
public interface BookMapper {
    List<Book> findAll();
}
