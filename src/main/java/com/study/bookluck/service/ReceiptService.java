package com.study.bookluck.service;

import com.study.bookluck.entity.Phrase;
import com.study.bookluck.entity.Receipt;
import com.study.bookluck.repository.PhraseMapper;
import com.study.bookluck.repository.ReceiptMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptMapper receiptMapper;

    public List<Receipt> getReceiptsByUserId(Integer userId) {
        return receiptMapper.findByUserId(userId);
    }
}
