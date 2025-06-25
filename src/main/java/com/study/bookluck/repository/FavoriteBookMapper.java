package com.study.bookluck.repository;

import com.study.bookluck.entity.Book;
import com.study.bookluck.entity.FavoriteBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FavoriteBookMapper {

    Optional<FavoriteBook> findByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);

    List<FavoriteBook> findByUserId(String userId);

    void insertFavorite(FavoriteBook favoriteBook);

    void deleteByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);

    void deleteByUserId(String userId);

    void deleteByBookId(String bookId);

    List<Book> findFavoriteBooksDetailsByUserId(@Param("userId") Integer userId);

}