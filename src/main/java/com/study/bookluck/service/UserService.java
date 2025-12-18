package com.study.bookluck.service;

import com.study.bookluck.entity.*;
import com.study.bookluck.repository.*;
import com.study.bookluck.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
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

}
