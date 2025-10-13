package com.study.bookluck.repository;

import com.study.bookluck.entity.Receipt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReceiptMapper {
    void insertReceipt(Receipt receipt);
    List<Receipt> findByUserId(@Param("userId") Integer userId);
}
