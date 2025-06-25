package com.study.bookluck.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bookluck.entity.Book;
import com.study.bookluck.entity.BookRecord;
import com.study.bookluck.entity.FavoriteBook;
import com.study.bookluck.repository.BookMapper;
import com.study.bookluck.repository.BookRecordMapper;
import com.study.bookluck.repository.FavoriteBookMapper;
import com.study.bookluck.entity.NaverResult;
import com.study.bookluck.dto.BookDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor

public class BookService {
    private final BookMapper bookMapper;
    private final BookRecordMapper bookRecordMapper;
    private final FavoriteBookMapper favoriteBookMapper;

    // 네이버 도서 검색 api key
    private final String CLIENT_ID = "LyhjhAt1_1TNffOFXhE2";
    private final String CLIENT_SECRET = "ZzDyQth967";
 
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }

    public List<Book> apiBooks(String keyword) {

        List<Book> books = null;

        try {
            // String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");

            // String apiURL = "https://openapi.naver.com/v1/search/book.json?query=" + encodedKeyword;
            // URL url = new URL(apiURL);

            URI url = UriComponentsBuilder
        		  .fromUriString("https://openapi.naver.com")
        		  .path("/v1/search/book.json")
        		  .queryParam("query", keyword)
        		  .queryParam("display", 100)
        		  .queryParam("start", 1)
        		  .queryParam("sort", "sim")
        		  .encode()
        		  .build()
        		  .toUri();

            // Spring 요청 제공 클래스
            RequestEntity<Void> req = RequestEntity
                                    .get(url)
                                    .header("X-Naver-Client-Id", CLIENT_ID)
                                    .header("X-Naver-Client-Secret", CLIENT_SECRET)
                                    .build();
            // Spring 제공 restTemplate
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

            // System.out.println(resp);
            
            // JSON 파싱 (Json 문자열을 객체로 만듦, 문서화)
            ObjectMapper om = new ObjectMapper();
            NaverResult result = null;

            try {
                result = om.readValue(resp.getBody(), NaverResult.class);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            
            System.out.println(result);
            books =result.getItems();	// books를 list.html에 출력 -> model 선언

            

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // MySQL에 저장
            if (!books.isEmpty()) {
                bookMapper.insertBooks(books);
                System.out.println("Books saved to database successfully!");
            } else {
                System.out.println("No books found.");
            }
        } catch (Exception e) {
            System.out.println("Error fetching or saving books: " + e.getMessage());
            // return ResponseEntity.status(500).body("Error fetching or saving books: " + e.getMessage());
        }

        return books;

    }

    @Transactional
    public boolean addBookToFavorites(String userId, String bookId) {
        Optional<FavoriteBook> existingFavorite = favoriteBookMapper.findByUserIdAndBookId(userId, bookId);
        if (existingFavorite.isPresent()) {
            return false; // 이미 추가됨
        }

        FavoriteBook favoriteBook = FavoriteBook.builder()
                .userId(userId)
                .bookId(bookId)
                .build();

        favoriteBookMapper.insertFavorite(favoriteBook);
        return true; // 성공적으로 추가
    }

    public List<String> getFavoriteBookIds(String userId) {
        return favoriteBookMapper.findByUserId(userId).stream()
                .map(FavoriteBook::getBookId)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean removeBookFromFavorites(String userId, String bookId) {
        Optional<FavoriteBook> existingFavorite = favoriteBookMapper.findByUserIdAndBookId(userId, bookId);
        if (existingFavorite.isPresent()) {
            favoriteBookMapper.deleteByUserIdAndBookId(userId, bookId);
            return true; // 성공적으로 삭제
        }
        return false; // 즐겨찾기에 없음
    }

    @Transactional
    public boolean addBookRecord(Integer userId, String status, String bookId, Integer duration, LocalDate endDate, String review) {

        BookRecord bookRecord = BookRecord.builder()
                .userId(userId)
                .status(status)
                .bookId(bookId)
                .duration(duration)
                .endDate(endDate)
                .review(review)
                .build();

        bookRecordMapper.insertBookRecord(bookRecord);

        return true; // 성공적으로 기록
    }

    // 특정 사용자의 모든 책 기록 조회 (추가 기능)
    public List<BookRecord> getUserBookRecords(Integer userId) {
        return bookRecordMapper.findByUserId(userId);
    }

    public List<BookDto> getUserFavoriteBooksDetails(Integer userId) {
        // MyBatis 사용 시
        List<Book> favoriteBooks = favoriteBookMapper.findFavoriteBooksDetailsByUserId(userId);
        return favoriteBooks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Spring Data JPA 사용 시 (위의 FavoriteBookRepository 참고)
        /*
        return favoriteBookRepository.findFavoriteBooksDetailsByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        */
    }

    private BookDto convertToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .link(book.getLink())
                .image(book.getImage())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .description(book.getDescription())
                .pubdate(book.getPubdate())
                .isbn(book.getIsbn())
                .discount(book.getDiscount())
                .price(book.getPrice())
                .category1(book.getCategory1())
                .category2(book.getCategory2())
                .build();
    }

}
