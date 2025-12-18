package com.study.bookluck.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bookluck.entity.*;
import com.study.bookluck.repository.*;
import com.study.bookluck.dto.*;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor

public class BookService {
    private final BookMapper bookMapper;
    private final BookRecordMapper bookRecordMapper;
    private final FavoriteBookMapper favoriteBookMapper;
    private final ChatGptService chatGptService;
    private final ReceiptMapper receiptMapper;
    private final UserService userService;

    // 네이버 도서 검색 api key
    private final String CLIENT_ID = "LyhjhAt1_1TNffOFXhE2";
    private final String CLIENT_SECRET = "ZzDyQth967";
 
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }

    public List<Book> apiBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        int display = 100;
        int total = 0;
    
        try {
            // 1차 호출로 total 확인
            URI firstUrl = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/book.json")
                    .queryParam("query", keyword)
                    .queryParam("display", display)
                    .queryParam("start", 1)
                    .queryParam("sort", "sim")
                    .encode()
                    .build()
                    .toUri();
    
            RequestEntity<Void> firstReq = RequestEntity
                    .get(firstUrl)
                    .header("X-Naver-Client-Id", CLIENT_ID)
                    .header("X-Naver-Client-Secret", CLIENT_SECRET)
                    .build();
    
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> firstResp = restTemplate.exchange(firstReq, String.class);
    
            ObjectMapper om = new ObjectMapper();
            NaverResult firstResult = om.readValue(firstResp.getBody(), NaverResult.class);
    
            if (firstResult.getItems() != null) {
                books.addAll(firstResult.getItems());
            }
    
            total = firstResult.getTotal();
            System.out.println("총 검색 결과 수: " + total);
    
            // 이후 호출 (start=101부터 시작)
            for (int start = 101; start <= total && start <= 1000; start += display) {
                URI url = UriComponentsBuilder
                        .fromUriString("https://openapi.naver.com")
                        .path("/v1/search/book.json")
                        .queryParam("query", keyword)
                        .queryParam("display", display)
                        .queryParam("start", start)
                        .queryParam("sort", "sim")
                        .encode()
                        .build()
                        .toUri();
    
                RequestEntity<Void> req = RequestEntity
                        .get(url)
                        .header("X-Naver-Client-Id", CLIENT_ID)
                        .header("X-Naver-Client-Secret", CLIENT_SECRET)
                        .build();
    
                ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
                NaverResult result = om.readValue(resp.getBody(), NaverResult.class);
    
                if (result.getItems() != null) {
                    books.addAll(result.getItems());
                }
    
                // 네이버 API는 1000개까지만 제공
                if (start + display > 1000) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // 알라딘 API 매칭
        List<AladinResult.Item> aladinItems = getAladinItems(keyword);
        if (!books.isEmpty() && !aladinItems.isEmpty()) {
            for (Book book : books) {
                Optional<AladinResult.Item> matched = aladinItems.stream()
                        .filter(ai -> ai.getIsbn13() != null && ai.getIsbn13().equals(book.getIsbn()))
                        .findFirst();
    
                matched.ifPresent(ai -> {
                    String aladinCategory = extractMainCategory(ai.getCategoryName());
                    book.setCategory1(aladinCategory);
                });
            }
        }

        // DB 저장
        try {
            if (!books.isEmpty()) {
                bookMapper.insertBooks(books);
                System.out.println("Books saved to database successfully!");
            } else {
                System.out.println("No books found.");
            }
        } catch (Exception e) {
            System.out.println("Error saving books: " + e.getMessage());
        }

        return books;
    }


    private String extractMainCategory(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return "기타";
        }
        // 예시: "국내도서>소설/시/희곡>소설" -> "소설"
        // 예시: "국내도서>인문학>인문일반" -> "인문학"
        String[] parts = categoryName.split(">");
        if (parts.length > 1) {
            return parts[1];
        }
        return categoryName;
    }

    public List<AladinResult.Item> getAladinItems(String keyword) {
        List<AladinResult.Item> allItems = new ArrayList<>();
        String apiKey = "ttbbookluck.team0025001";
        int start = 1;
        int maxResults = 100;

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper om = new ObjectMapper();

            while (true) {
                URI url = UriComponentsBuilder
                        .fromUriString("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
                        .queryParam("ttbkey", apiKey)
                        .queryParam("Query", keyword)
                        .queryParam("QueryType", "Keyword")
                        .queryParam("MaxResults", maxResults)
                        .queryParam("start", start)
                        .queryParam("SearchTarget", "Book")
                        .queryParam("output", "JS")
                        .queryParam("Version", "20131101")
                        .encode()
                        .build()
                        .toUri();

                ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
                AladinResult result = om.readValue(resp.getBody(), AladinResult.class);

                if (result.getItem() == null || result.getItem().isEmpty()) {
                    break;
                }

                allItems.addAll(result.getItem());

                // 마지막 페이지라면 중단
                if (result.getItem().size() < maxResults) {
                    break;
                }

                start += maxResults;

                // 너무 많이 불러오는 걸 방지 (예: 최대 1000권)
                if (start > 1000) break;

                // 호출 간 간격을 줄 수도 있음 (예: Thread.sleep(100))
            }

            System.out.println("총 알라딘 검색 결과 수: " + allItems.size());
            return allItems;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
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
    public boolean addBookRecord(Integer userId, String status, String bookId, Integer duration, String endDate, String review) {

        BookRecord bookRecord = BookRecord.builder()
                .userId(userId)
                .status(status)
                .bookId(bookId)
                .duration(duration)
                .endDate(endDate)
                .review(review)
                .build();

        bookRecordMapper.insertBookRecord(bookRecord);

        // ✅ status가 FINISHED라면 receipt 생성 로직 실행
        if ("FINISHED".equalsIgnoreCase(status)) {
            List<BookRecord> pastReviews = bookRecordMapper.findByUserAndBookId(userId, bookId);
            // System.out.println(pastReviews);

            // 모든 리뷰 텍스트 합치기
            String combinedReview = pastReviews.stream()
                    .map(BookRecord::getReview)
                    .filter(r -> r != null && !r.isEmpty())
                    .collect(Collectors.joining(" "));

            // 모든 기록의 duration 합산
            int totalDuration = pastReviews.stream()
                    .mapToInt(BookRecord::getDuration)
                    .sum();

            // Optional을 사용해 첫 번째 요소와 endDate를 안전하게 가져옴
            String startDate = pastReviews.stream()
                    .findFirst()
                    .map(BookRecord::getEndDate)
                    .orElse(null); // endDate가 없으면 null 반환

            // System.out.println(pastReviews.stream().findFirst());
            System.out.println("계산된 startDate 값: " + startDate);


            // 요약 생성
            String summary = chatGptService.summarizeReview(combinedReview);

            // receipt 저장
            Receipt receipt = Receipt.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .startDate(startDate)
                    .endDate(endDate)
                    .duration(totalDuration)
                    .review(summary)
                    .build();

            receiptMapper.insertReceipt(receipt);

            userService.checkBadge(userId, 1);
        }

        return true; // 성공적으로 기록
    }

    @Transactional
    public boolean deleteBookRecord(Long id, Integer userId) {
        return bookRecordMapper.deleteBookRecord(id, userId) > 0;
    }

    public void updateBookReview(Integer userId, Long recordId, String review) {
        bookRecordMapper.updateBookReview(userId, recordId, review);
    }

    // 특정 사용자의 모든 책 기록 조회 (추가 기능)
    public List<BookRecord> getUserBookRecords(Integer userId, String status) {
        if (status == null || status.isEmpty()) {
            return bookRecordMapper.findByUserId(userId);
        } else {
            return bookRecordMapper.findByUserIdAndStatus(userId, status);
        }
    }

    public List<BookDto> getUserFavoriteBooksDetails(Integer userId) {
        // MyBatis 사용 시
        List<Book> favoriteBooks = favoriteBookMapper.findFavoriteBooksDetailsByUserId(userId);
        return favoriteBooks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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

    public ReadingStatsDto getReadingStats(Integer userId, Integer year) {

        // 1. 데이터 조회
        List<BookRecord> records = bookRecordMapper.findRecordsByUserIdAndYear(userId, year);

        // 2. 월별 초기값 생성 (1~12월 모두 0)
        Map<Integer, Integer> monthMap = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthMap.put(i, 0);
        }

        // 3. 월별 duration 합산
        for (BookRecord br : records) {
            LocalDate end = LocalDate.parse(br.getEndDate());  // "2025-03-12" 같은 형태일 경우
            int month = end.getMonthValue();  // 3

            monthMap.put(month, monthMap.get(month) + br.getDuration());
        }

        // 4. total minutes
        int totalMinutes = monthMap.values().stream().mapToInt(v -> v).sum();

        // 5. DTO 변환
        List<ReadingStatsDto.MonthData> monthDataList = monthMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> ReadingStatsDto.MonthData.builder()
                    .month(String.valueOf(entry.getKey()))
                    .minutes(entry.getValue())
                    .build()
            ).toList();

        return ReadingStatsDto.builder()
                .year(year)
                .readingMinutes(totalMinutes)
                .data(monthDataList)
                .build();
    }

    public List<ReadingTimeResponse> getWeeklyReadingTime(Integer userId) {

        List<BookRecord> records = bookRecordMapper.findLast7DaysRecords(userId);

        Map<String, Double> dayToHours = new LinkedHashMap<>();

        // 최근 7일 날짜 초기값 생성
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            String key = day.format(DateTimeFormatter.ofPattern("MM.dd"));
            dayToHours.put(key, 0.0);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (BookRecord br : records) {

            LocalDate date = LocalDate.parse(br.getEndDate(), formatter);
            String key = date.format(DateTimeFormatter.ofPattern("MM.dd"));

            double hours = br.getDuration() / 60.0;

            dayToHours.put(key, dayToHours.getOrDefault(key, 0.0) + hours);
        }

        List<ReadingTimeResponse> result = new ArrayList<>();

        dayToHours.forEach((date, hours) ->
                result.add(new ReadingTimeResponse(date, Math.round(hours * 10) / 10.0))
        );

        return result;
    }

    


}
