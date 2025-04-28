package com.study.bookluck.controller;

import com.study.bookluck.service.BookService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/books/addToFavorites")
    public ResponseEntity<?> addToFavorites(@RequestBody Map<String, Object> payload) {
        String bookId = (String) payload.get("bookId");
        String userId = (String) payload.get("userId"); // 실제로는 사용자 인증 정보를 통해 얻어야 합니다.

        userId = "1";

        boolean isSuccess = bookService.addBookToFavorites(userId, bookId);

        if (isSuccess) {
            return new ResponseEntity<>("즐겨찾기에 추가되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("이미 즐겨찾기에 추가되었거나 추가에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 추가 기능: 특정 사용자의 즐겨찾기 목록 조회
    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<List<String>> getUserFavorites(@PathVariable("userId") String userId) {
        List<String> favoriteBookIds = bookService.getFavoriteBookIds(userId);
        return ResponseEntity.ok(favoriteBookIds);
    }

    // 추가 기능: 특정 사용자의 즐겨찾기 삭제
    @DeleteMapping("/users/{userId}/favorites/{bookId}")
    public ResponseEntity<String> removeFavoriteBook(@PathVariable String userId, @PathVariable String bookId) {
        boolean isRemoved = bookService.removeBookFromFavorites(userId, bookId);
        if (isRemoved) {
            return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
        } else {
            return new ResponseEntity<>("즐겨찾기에서 삭제에 실패했거나 해당 항목이 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}

