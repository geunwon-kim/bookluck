package com.study.bookluck.controller;

import com.study.bookluck.service.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;
 
    @GetMapping("/books/getAllBooks")
    @ResponseBody
    public ResponseEntity getBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping("/books-post/getAllBooks")
    @ResponseBody
	public ResponseEntity getBooks_post() {
		return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/books/getApiBooks")
    @ResponseBody
	public ResponseEntity<?> getBooks_API(@RequestParam(name = "keyword") String keyword) {
		return ResponseEntity.ok(bookService.apiBooks(keyword));
    }
}

