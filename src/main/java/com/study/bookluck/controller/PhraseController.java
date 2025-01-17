package com.study.bookluck.controller;

import com.study.bookluck.service.PhraseService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RequiredArgsConstructor
@RestController
public class PhraseController {
    private final PhraseService phraseService;
 
    @GetMapping("/phrases")
    @ResponseBody
    public ResponseEntity getPhrases() {
        return ResponseEntity.ok(phraseService.getAllPhrases());
    }
}
