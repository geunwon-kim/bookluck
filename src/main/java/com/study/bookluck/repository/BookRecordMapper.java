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
    int deleteBookRecord(@Param("id") Long id, @Param("userId") Integer userId);
    void updateBookReview(@Param("userId") Integer userId,@Param("recordId") Long recordId,@Param("review") String review);

    List<BookRecord> findByUserId(@Param("userId") Integer userId);
    List<BookRecord> findByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);
    List<BookRecord> findByBookId(@Param("bookId") String bookId);

    List<BookRecord> findByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") String bookId);


    // void deleteBookRecord(@Param("id") Long id);
}
