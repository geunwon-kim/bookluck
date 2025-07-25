package com.study.bookluck.repository;

import com.study.bookluck.entity.Book;
import com.study.bookluck.entity.BookRecord;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
@Mapper
public interface BookRecordMapper {
    void insertBookRecord(BookRecord bookRecord);

    List<BookRecord> findByUserId(@Param("userId") Integer userId); // String -> Integer로 변경

    List<BookRecord> findByBookId(@Param("bookId") String bookId);

    List<BookRecord> findByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") String bookId); // String -> Integer로 변경


    void deleteBookRecord(@Param("id") Long id);
}
