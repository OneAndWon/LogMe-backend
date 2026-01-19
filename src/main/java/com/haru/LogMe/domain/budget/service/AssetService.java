package com.haru.LogMe.domain.budget.service;

import com.haru.LogMe.domain.budget.dto.AssetRequest;
import com.haru.LogMe.domain.budget.dto.AssetResponse;
import com.haru.LogMe.domain.budget.entity.Asset;
import com.haru.LogMe.domain.budget.repository.AssetRepository;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {
    private final AssetRepository assetRepository;

    @Transactional
    public AssetResponse createAsset(User user, AssetRequest.AssetCreateDto request) {
        Asset asset = Asset.builder()
                .user(user)
                .name(request.getName())
                .type(request.getType())
                .initialBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .build();
        return AssetResponse.from(assetRepository.save(asset));
    }

    public List<AssetResponse> getAssets(User user) {
        // Repository에 findAllByUser(User user) 메서드 추가 필요
        return assetRepository.findAll().stream()
                .filter(a -> a.getUser().getUserId().equals(user.getUserId())) // 임시 필터
                .map(AssetResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssetResponse updateAsset(Long assetId, User user, AssetRequest.AssetUpdateDto request) {
        Asset asset = assetRepository.findByAssetIdAndUser(assetId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSET_NOT_FOUND));
        asset.update(request.getName(), request.getType(), request.getInitialBalance());
        return AssetResponse.from(asset);
    }

    @Transactional
    public void deleteAsset(Long assetId, User user) {
        Asset asset = assetRepository.findByAssetIdAndUser(assetId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSET_NOT_FOUND));
        assetRepository.delete(asset);
    }
}
