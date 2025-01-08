package com.study.bookluck.service;

import com.study.bookluck.entity.Book;
import com.study.bookluck.repository.BookMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor

public class BookService {
    private final BookMapper bookMapper;
 
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }
}
