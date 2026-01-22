package com.study.bookluck.repository;

import com.study.bookluck.entity.ReadingSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ReadingSessionMapper {
    ReadingSession findActiveSession(@Param("userId") Integer userId);
    ReadingSession findStoppedSession(@Param("userId") Integer userId);
    void insertSession(ReadingSession session);
    void updateSession(ReadingSession session);
}
