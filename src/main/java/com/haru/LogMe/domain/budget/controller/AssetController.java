package com.haru.LogMe.domain.budget.controller;

import com.haru.LogMe.domain.budget.dto.AssetRequest;
import com.haru.LogMe.domain.budget.dto.AssetResponse;
import com.haru.LogMe.domain.budget.service.AssetService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logme/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @PostMapping
    public ApiResponse<AssetResponse> createAsset(@AuthenticationPrincipal User user, @Valid @RequestBody AssetRequest.AssetCreateDto request) {
        return ApiResponse.ok(assetService.createAsset(user, request));
    }

    @GetMapping
    public ApiResponse<List<AssetResponse>> getAssets(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(assetService.getAssets(user));
    }

    @PutMapping("/{assetId}")
    public ApiResponse<AssetResponse> updateAsset(@PathVariable Long assetId, @AuthenticationPrincipal User user, @RequestBody AssetRequest.AssetUpdateDto request) {
        return ApiResponse.ok(assetService.updateAsset(assetId, user, request));
    }

    @DeleteMapping("/{assetId}")
    public ApiResponse<Void> deleteAsset(@PathVariable Long assetId, @AuthenticationPrincipal User user) {
        assetService.deleteAsset(assetId, user);
        return ApiResponse.ok(null);
    }
}
