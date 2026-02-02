package com.haru.LogMe.domain.budget.dto;

import com.haru.LogMe.domain.budget.entity.Asset;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AssetResponse {
    private Long assetId;
    private String name;
    private String type;
    private BigDecimal initialBalance;

    public static AssetResponse from(Asset asset) {
        return AssetResponse.builder()
                .assetId(asset.getAssetId())
                .name(asset.getName())
                .type(asset.getType())
                .initialBalance(asset.getInitialBalance())
                .build();
    }
}
