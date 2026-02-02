package com.haru.LogMe.domain.budget.controller;

import com.haru.LogMe.domain.budget.dto.TransactionRequest;
import com.haru.LogMe.domain.budget.dto.TransactionResponse;
import com.haru.LogMe.domain.budget.service.TransactionService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import com.haru.LogMe.global.response.ListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logme/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    // 1. 생성
    @PostMapping
    public ApiResponse<TransactionResponse> createTransaction(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransactionRequest.CreateDto request) {

        TransactionResponse response = transactionService.createTransaction(user, request);
        return ApiResponse.ok(response);
    }

    // 2. 목록 조회
    @GetMapping
    public ApiResponse<ListResponse<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal User user) {

        return ApiResponse.ok(transactionService.getTransactions(user));
    }

    // 3. 상세 조회
    @GetMapping("/{transactionId}")
    public ApiResponse<TransactionResponse> getTransactionDetail(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal User user) {

        return ApiResponse.ok(transactionService.getTransactionDetail(transactionId, user));
    }

    // 4. 수정
    @PutMapping("/{transactionId}")
    public ApiResponse<TransactionResponse> updateTransaction(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal User user,
            @RequestBody TransactionRequest.UpdateDto request) {

        TransactionResponse response = transactionService.updateTransaction(transactionId, user, request);
        return ApiResponse.ok(response);
    }

    // 5. 삭제
    @DeleteMapping("/{transactionId}")
    public ApiResponse<Void> deleteTransaction(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal User user) {

        transactionService.deleteTransaction(transactionId, user);
        return ApiResponse.ok(null);
    }
}
