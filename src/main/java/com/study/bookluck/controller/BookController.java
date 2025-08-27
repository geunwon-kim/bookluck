package com.study.bookluck.controller;

import com.study.bookluck.entity.BookRecord;
import com.study.bookluck.service.BookService;
import com.study.bookluck.dto.BookDto;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
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

    @PostMapping("/books/record")
    public ResponseEntity<?> recordBookReading(@RequestBody Map<String, Object> payload) {
        Integer userId; // String -> Integer로 변경
        try {
            // payload.get("userId")는 기본적으로 Object 타입이므로 Integer로 캐스팅
            // JSON에서 숫자를 문자열로 보낼 수도 있으므로, String으로 받은 후 Integer.parseInt() 고려
            Object userIdObj = payload.get("userId");
            if (userIdObj instanceof Integer) {
                userId = (Integer) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Integer.parseInt((String) userIdObj);
            } else {
                return new ResponseEntity<>("userId 형식이 올바르지 않습니다. (정수 형식)", HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("userId를 정수형으로 변환할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        String status = (String) payload.get("status"); // status 추가
        String bookId = (String) payload.get("bookId");
        Integer duration = (Integer) payload.get("duration");
        String endDateStr = (String) payload.get("endDate");
        String review = (String) payload.get("review"); // review 추가

        // 필수 파라미터 검사 (status는 항상 필수)
        if (userId == null || status == null || bookId == null) {
            return new ResponseEntity<>("필수 파라미터(userId, status, bookId)가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }

        LocalDate endDate = null;
        // status가 'FINISHED'일 경우 duration과 endDate는 필수로 간주
        if ("FINISHED".equalsIgnoreCase(status)) {
            if (duration == null || endDateStr == null) {
                return new ResponseEntity<>("완독 상태(FINISHED)의 경우 duration과 endDate는 필수입니다.", HttpStatus.BAD_REQUEST);
            }
            try {
                endDate = LocalDate.parse(endDateStr); // 문자열을 LocalDate로 파싱
            } catch (Exception e) {
                return new ResponseEntity<>("endDate 형식이 올바르지 않습니다. (YYYY-MM-DD 형식)", HttpStatus.BAD_REQUEST);
            }
        }
        // status가 'READING' 또는 'DROPPED'일 경우 duration, endDate, review는 선택 사항
        // 이 경우 null로 넘겨도 됨.

        boolean isSuccess = bookService.addBookRecord(userId, status, bookId, duration, endDate, review);

        if (isSuccess) {
            return new ResponseEntity<>("책 읽기 기록이 성공적으로 추가되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("책 읽기 기록 추가에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{userId}/records")
    public ResponseEntity<List<BookRecord>> getUserBookRecords(@PathVariable("userId") Integer userId, @RequestParam(value = "status", required = false) String status) {
        List<BookRecord> records = bookService.getUserBookRecords(userId, status);
        return ResponseEntity.ok(records);
    }

    @DeleteMapping("/users/{userId}/records/{id}")
    public ResponseEntity<String> deleteBookRecord(@PathVariable("userId") Integer userId,@PathVariable("id") Long id) {
        boolean deleted = bookService.deleteBookRecord(id, userId);
        if (deleted) {
            return ResponseEntity.ok("Book record deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book record not found or not owned by user.");
        }
    }

    @GetMapping("/users/{userId}/favorites/details")
    public ResponseEntity<List<BookDto>> getUserFavoriteBooksDetails(@PathVariable("userId") Integer userId) {
        List<BookDto> favoriteBooks = bookService.getUserFavoriteBooksDetails(userId);
        if (favoriteBooks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(favoriteBooks, HttpStatus.OK);
    }


}

