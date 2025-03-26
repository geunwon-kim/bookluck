package com.study.bookluck.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bookluck.entity.Book;
import com.study.bookluck.repository.BookMapper;
import com.study.bookluck.entity.NaverResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
 
@Service
@RequiredArgsConstructor

public class BookService {
    private final BookMapper bookMapper;

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
        		  .queryParam("display", 10)
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

            System.out.println(resp);
            
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
}
