package com.study.bookluck.service;

import com.study.bookluck.entity.Phrase;
import com.study.bookluck.repository.PhraseMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class PhraseService {
    private final PhraseMapper phraseMapper;
 
    public List<Phrase> getAllPhrases() {
        return phraseMapper.findAll();
    }
}
