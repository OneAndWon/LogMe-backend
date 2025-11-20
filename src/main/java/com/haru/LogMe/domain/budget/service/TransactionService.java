package com.haru.LogMe.domain.budget.service;

import com.haru.LogMe.domain.budget.dto.TransactionRequest;
import com.haru.LogMe.domain.budget.dto.TransactionResponse;
import com.haru.LogMe.domain.budget.entity.Asset;
import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import com.haru.LogMe.domain.budget.entity.Transaction;
import com.haru.LogMe.domain.budget.repository.AssetRepository;
import com.haru.LogMe.domain.budget.repository.FinanceCategoryRepository;
import com.haru.LogMe.domain.budget.repository.TransactionRepository;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;
    private final FinanceCategoryRepository categoryRepository;

    // 1. 생성
    @Transactional
    public TransactionResponse createTransaction(User user, TransactionRequest.CreateDto request) {
        // Asset 검증 (사용자 소유인지)
        Asset asset = null;
        if (request.getAssetId() != null) {
            asset = assetRepository.findByAssetIdAndUser(request.getAssetId(), user)
                    .orElseThrow(() -> new CustomException(ErrorCode.ASSET_NOT_FOUND));
        }

        // Category 검증 (사용자 소유인지)
        FinanceCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByFinanceCategoryIdAndUser(request.getCategoryId(), user)
                    .orElseThrow(() -> new CustomException(ErrorCode.FINANCE_CATEGORY_NOT_FOUND));
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .asset(asset)
                .category(category)
                .type(request.getType())
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .memo(request.getMemo())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponse.from(savedTransaction);
    }

    // 2. 목록 조회
    public List<TransactionResponse> getTransactions(User user) {
        return transactionRepository.findAllByUserOrderByDateDesc(user).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    // 3. 수정
    @Transactional
    public TransactionResponse updateTransaction(Long transactionId, User user, TransactionRequest.UpdateDto request) {
        Transaction transaction = transactionRepository.findByTransactionIdAndUser(transactionId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        // Asset 검증
        Asset asset = transaction.getAsset(); // 기존 유지
        if (request.getAssetId() != null) {
            asset = assetRepository.findByAssetIdAndUser(request.getAssetId(), user)
                    .orElseThrow(() -> new CustomException(ErrorCode.ASSET_NOT_FOUND));
        }

        // Category 검증
        FinanceCategory category = transaction.getCategory(); // 기존 유지
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByFinanceCategoryIdAndUser(request.getCategoryId(), user)
                    .orElseThrow(() -> new CustomException(ErrorCode.FINANCE_CATEGORY_NOT_FOUND));
        }

        // Dirty Checking 업데이트
        transaction.update(
                asset,
                category,
                request.getType() != null ? request.getType() : transaction.getType(),
                request.getAmount() != null ? request.getAmount() : transaction.getAmount(),
                request.getDate() != null ? request.getDate() : transaction.getDate(),
                request.getDescription() != null ? request.getDescription() : transaction.getDescription(),
                request.getMemo() != null ? request.getMemo() : transaction.getMemo()
        );

        return TransactionResponse.from(transaction);
    }

    // 4. 삭제
    @Transactional
    public void deleteTransaction(Long transactionId, User user) {
        Transaction transaction = transactionRepository.findByTransactionIdAndUser(transactionId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        transactionRepository.delete(transaction);
    }
}
