package com.study.bookluck.service;

import com.study.bookluck.entity.*;
import com.study.bookluck.repository.*;
import com.study.bookluck.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BadgeMapper badgeMapper;
 
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public List<BadgeResponse> getBadgeData(Integer userId) {

        List<UserBadge> list = badgeMapper.findUserBadges(userId);

        return list.stream()
                .map(b -> new BadgeResponse(
                        b.getBadgeId(),
                        b.getTitle(),
                        b.getUserId() != null   // 있으면 획득
                ))
                .toList();
    }

    public void checkBadge(Integer userId, Integer badgeId) {

        int count = 0;

        count = badgeMapper.countUserBadge(userId, badgeId);
        if (count == 0) {
            badgeMapper.insertUserBadge(
                UserBadge.builder()
                    .userId(userId)
                    .badgeId(badgeId)
                    .build()
            );
        }
    }

    /**
     * 구글 OAuth2 로그인/회원가입 처리
     * @param googleId 구글 계정 ID
     * @param email 이메일
     * @param userName 사용자 이름
     * @return LoginResponse 로그인 응답 정보
     */
    @Transactional
    public LoginResponse loginOrRegisterWithGoogle(String googleId, String email, String userName) {
        // 기존 사용자 확인 (구글 ID로 먼저 확인)
        User existingUser = userMapper.findByGoogleId(googleId);
        
        if (existingUser != null) {
            // 기존 사용자 로그인
            return new LoginResponse(
                existingUser.getUser_id(),
                existingUser.getUser_name(),
                existingUser.getEmail(),
                false  // 기존 사용자
            );
        }
        
        // 이메일로도 확인 (다른 로그인 방식으로 가입했을 수 있음)
        existingUser = userMapper.findByEmail(email);
        if (existingUser != null) {
            // 기존 사용자에 구글 ID 추가
            existingUser.setGoogle_id(googleId);
            userMapper.update(existingUser);
            
            return new LoginResponse(
                existingUser.getUser_id(),
                existingUser.getUser_name(),
                existingUser.getEmail(),
                false  // 기존 사용자
            );
        }
        
        // 신규 회원가입
        User newUser = new User();
        newUser.setGoogle_id(googleId);
        newUser.setEmail(email);
        newUser.setUser_name(userName);
        newUser.setCreated_at(LocalDateTime.now());
        newUser.setUpdated_at(LocalDateTime.now());
        
        userMapper.insert(newUser);
        
        return new LoginResponse(
            newUser.getUser_id(),
            newUser.getUser_name(),
            newUser.getEmail(),
            true  // 신규 사용자
        );
    }

    /**
     * 구글 ID로 사용자 조회
     * @param googleId 구글 계정 ID
     * @return User 사용자 정보
     */
    public User findByGoogleId(String googleId) {
        return userMapper.findByGoogleId(googleId);
    }

}
