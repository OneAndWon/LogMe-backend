package com.haru.LogMe.domain.budget.service;

import com.haru.LogMe.domain.budget.dto.FinanceCategoryRequest;
import com.haru.LogMe.domain.budget.dto.FinanceCategoryResponse;
import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import com.haru.LogMe.domain.budget.repository.FinanceCategoryRepository;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import com.haru.LogMe.global.response.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceCategoryService {
    private final FinanceCategoryRepository categoryRepository;

    //카테고리 생성
    @Transactional
    public FinanceCategoryResponse createCategory(User user, FinanceCategoryRequest.FinanceCategoryCreateDto request) {
        FinanceCategory category = FinanceCategory.builder()
                .user(user)
                .name(request.getName())
                .type(request.getType()) // INCOME, EXPENSE
                .icon(request.getIcon())
                .build();

        FinanceCategory savedCategory = categoryRepository.save(category);
        return FinanceCategoryResponse.from(savedCategory);
    }

    //카테고리 목록 조회
    public ListResponse<FinanceCategoryResponse> getCategories(User user, String type) {
        List<FinanceCategoryResponse> list = categoryRepository.findAllByUser(user).stream()
                .filter(category -> type == null || category.getType().equalsIgnoreCase(type))
                .map(FinanceCategoryResponse::from)
                .collect(Collectors.toList());

        return ListResponse.of(list);
    }

    // 3. 카테고리 수정
    @Transactional
    public FinanceCategoryResponse updateCategory(Long categoryId, User user, FinanceCategoryRequest.FinanceCategoryUpdateDto request) {
        FinanceCategory category = categoryRepository.findByFinanceCategoryIdAndUser(categoryId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.FINANCE_CATEGORY_NOT_FOUND));

        category.update(
                request.getName(),
                request.getType(),
                request.getIcon()
        );

        return FinanceCategoryResponse.from(category);
    }

    // 4. 카테고리 삭제
    @Transactional
    public void deleteCategory(Long categoryId, User user) {
        FinanceCategory category = categoryRepository.findByFinanceCategoryIdAndUser(categoryId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.FINANCE_CATEGORY_NOT_FOUND));

        // 주의: 이 카테고리를 사용 중인 Transaction이 있다면,
        // 1) 삭제를 막거나 2) Transaction의 category_id를 null로 바꾸는 로직이 추가로 필요할 수 있음.
        // 현재는 단순 삭제로 구현.
        categoryRepository.delete(category);
    }
}
