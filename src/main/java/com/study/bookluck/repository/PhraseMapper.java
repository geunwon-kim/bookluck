package com.study.bookluck.repository;

import com.study.bookluck.entity.Phrase;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
@Mapper
public interface PhraseMapper {
    List<Phrase> findAll();
    Phrase findPhrase(int id);
}
