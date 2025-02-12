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

    public Phrase getRandomPhrase(){
        int rand_num = (int)(Math.random() * 45)+1;

        return phraseMapper.findPhrase(rand_num);
    }
}
