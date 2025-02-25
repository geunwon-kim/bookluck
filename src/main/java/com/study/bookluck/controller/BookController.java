package com.study.bookluck.controller;

import com.study.bookluck.service.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;
 
    @GetMapping("/books")
    @ResponseBody
    public ResponseEntity getBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping("/books-post")
    @ResponseBody
	public ResponseEntity getBooks_post() {
		return ResponseEntity.ok(bookService.getAllBooks());
    }
}
