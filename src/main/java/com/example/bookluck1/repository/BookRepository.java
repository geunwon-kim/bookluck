package com.example.bookluck1.repository;

import com.example.bookluck1.domain.Book;

import java.util.List;

public interface BookRepository {

    void save(Book book);
    Book findById(Long id);
    List<Book> findAll();
    void updateById(Long id, Book book);
    void deleteById(Long id);

}