package com.study.bookluck.controller;

import com.study.bookluck.entity.*;
import com.study.bookluck.service.*;
import com.study.bookluck.dto.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ReceiptService receiptService;
 
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{userId}/receipt")
    @ResponseBody
    public ResponseEntity<List<Receipt>> getReceiptsByUserId(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(receiptService.getReceiptsByUserId(userId));
    }

    @GetMapping("/users/{userId}/badges")
    public List<BadgeResponse> getUserBadges(@PathVariable("userId") Integer userId) {
        return userService.getBadgeData(userId);
    }

    /**
     * 구글 OAuth2 로그인 성공 후 콜백 처리
     * @param oauth2User OAuth2 인증된 사용자 정보
     * @return LoginResponse 로그인 응답 정보
     */
    @GetMapping("/login/success")
    public ResponseEntity<LoginResponse> loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 구글 사용자 정보 추출
        String googleId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String userName = oauth2User.getAttribute("name");
        
        if (userName == null || userName.isEmpty()) {
            userName = email != null ? email.split("@")[0] : "User";
        }

        // 로그인 또는 회원가입 처리
        LoginResponse response = userService.loginOrRegisterWithGoogle(googleId, email, userName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 구글 OAuth2 로그인 실패 처리
     * @return 에러 메시지
     */
    @GetMapping("/login/failure")
    public ResponseEntity<Map<String, String>> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "구글 로그인에 실패했습니다."));
    }

    /**
     * 구글 로그인 페이지로 리다이렉트하는 엔드포인트
     * @return 리다이렉트 URL
     */
    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> loginGoogle() {
        return ResponseEntity.ok(Map.of(
            "redirectUrl", 
            "/oauth2/authorization/google"
        ));
    }

}
