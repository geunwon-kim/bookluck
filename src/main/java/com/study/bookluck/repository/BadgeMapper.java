package com.study.bookluck.repository;

import com.study.bookluck.entity.UserBadge;
import com.study.bookluck.entity.Badge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
@Mapper
public interface BadgeMapper {

    List<Badge> findAllBadges();

    List<UserBadge> findUserBadges(@Param("userId") Integer userId);

    void insertUserBadge(UserBadge userBadge);
    
    int countUserBadge(@Param("userId") Integer userId, @Param("badgeId") Integer badgeId);
}
