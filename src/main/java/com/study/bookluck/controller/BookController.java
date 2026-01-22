package com.study.bookluck.controller;

import com.study.bookluck.entity.BookRecord;
import com.study.bookluck.entity.ReadingSession;
import com.study.bookluck.service.*;
import com.study.bookluck.dto.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

        String status = (String) payload.get("status");
        String bookId = (String) payload.get("bookId");
        Integer duration = (Integer) payload.get("duration");
        String endDate = (String) payload.get("endDate");
        String review = (String) payload.get("review");

        // 필수 파라미터 검사
        if (userId == null || status == null || bookId == null) {
            return new ResponseEntity<>("필수 파라미터(userId, status, bookId)가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }

        // 독서 세션이 있으면 duration과 endDate는 자동으로 설정되므로 선택 사항
        // 독서 세션이 없을 경우에만 duration과 endDate가 필수

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

    @PatchMapping("/users/{userId}/records/{recordId}/review")
    public ResponseEntity<Void> updateBookReview(@PathVariable("userId") Integer userId, @PathVariable("recordId") Long recordId, @RequestBody Map<String, String> request) {
        String review = request.get("review");
        bookService.updateBookReview(userId, recordId, review);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/users/{userId}/favorites/details")
    public ResponseEntity<List<BookDto>> getUserFavoriteBooksDetails(@PathVariable("userId") Integer userId) {
        List<BookDto> favoriteBooks = bookService.getUserFavoriteBooksDetails(userId);
        if (favoriteBooks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(favoriteBooks, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/records/stats")
    public ResponseEntity<ReadingStatsDto> getReadingStats(@PathVariable("userId") Integer userId, @RequestParam(value = "year") Integer year) {
        ReadingStatsDto stats = bookService.getReadingStats(userId, year);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users/{userId}/records/weekly")
    public List<ReadingTimeResponse> getReadingTime(@PathVariable("userId") Integer userId) {
        return bookService.getWeeklyReadingTime(userId);
    }

    /**
     * 독서 세션 시작
     * POST /books/reading/start
     */
    @PostMapping("/books/reading/start")
    public ResponseEntity<?> startReading(@RequestBody Map<String, Object> payload) {
        try {
            Integer userId = extractUserId(payload);
            
            if (userId == null) {
                return new ResponseEntity<>("필수 파라미터(userId)가 누락되었습니다.", HttpStatus.BAD_REQUEST);
            }
            
            ReadingSessionResponse session = bookService.startReadingSession(userId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 독서 일시정지
     * POST /books/reading/pause
     */
    @PostMapping("/books/reading/pause")
    public ResponseEntity<?> pauseReading(@RequestBody Map<String, Object> payload) {
        try {
            Integer userId = extractUserId(payload);
            
            if (userId == null) {
                return new ResponseEntity<>("필수 파라미터(userId)가 누락되었습니다.", HttpStatus.BAD_REQUEST);
            }
            
            ReadingSessionResponse session = bookService.pauseReadingSession(userId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 독서 재개
     * POST /books/reading/resume
     */
    @PostMapping("/books/reading/resume")
    public ResponseEntity<?> resumeReading(@RequestBody Map<String, Object> payload) {
        try {
            Integer userId = extractUserId(payload);
            
            if (userId == null) {
                return new ResponseEntity<>("필수 파라미터(userId)가 누락되었습니다.", HttpStatus.BAD_REQUEST);
            }
            
            ReadingSessionResponse session = bookService.resumeReadingSession(userId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 독서 종료 (deprecated - /books/record에서 자동으로 처리됨)
     * POST /books/reading/stop
     * @deprecated 이 엔드포인트는 더 이상 사용되지 않습니다. /books/record를 호출하면 자동으로 독서 세션이 종료됩니다.
     */
    @Deprecated
    @PostMapping("/books/reading/stop")
    public ResponseEntity<?> stopReading(@RequestBody Map<String, Object> payload) {
        try {
            Integer userId = extractUserId(payload);
            
            if (userId == null) {
                return new ResponseEntity<>("필수 파라미터(userId)가 누락되었습니다.", HttpStatus.BAD_REQUEST);
            }
            
            ReadingSession session = bookService.stopReadingSession(userId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 현재 독서 세션 조회
     * GET /books/reading/current/{userId}
     */
    @GetMapping("/books/reading/current/{userId}")
    public ResponseEntity<?> getCurrentReadingSession(@PathVariable("userId") Integer userId) {
        ReadingSessionResponse session = bookService.getCurrentReadingSession(userId);
        
        if (session == null) {
            return new ResponseEntity<>(Map.of("message", "활성 독서 세션이 없습니다."), HttpStatus.NOT_FOUND);
        }
        
        return ResponseEntity.ok(session);
    }

    /**
     * userId 추출 헬퍼 메서드
     */
    private Integer extractUserId(Map<String, Object> payload) {
        Object userIdObj = payload.get("userId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        } else if (userIdObj instanceof String) {
            return Integer.parseInt((String) userIdObj);
        }
        return null;
    }

}

