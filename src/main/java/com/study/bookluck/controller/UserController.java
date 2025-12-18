package com.study.bookluck.controller;

import com.study.bookluck.entity.*;
import com.study.bookluck.service.*;
import com.study.bookluck.dto.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ReceiptService receiptService;
 
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{userId}/receipt")
    @ResponseBody
    public ResponseEntity<List<Receipt>> getReceiptsByUserId(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(receiptService.getReceiptsByUserId(userId));
    }

    @GetMapping("/users/{userId}/badges")
    public List<BadgeResponse> getUserBadges(@PathVariable("userId") Integer userId) {
        return userService.getBadgeData(userId);
    }

}
