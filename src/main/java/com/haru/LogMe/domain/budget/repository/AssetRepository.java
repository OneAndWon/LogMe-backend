package com.haru.LogMe.domain.budget.repository;

import com.haru.LogMe.domain.budget.entity.Asset;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    // 본인의 자산 목록 전체 조회
    List<Asset> findAllByUser(User user);

    // 자산 상세 조회/수정/삭제 시 본인 확인을 위해 User 함께 조회
    Optional<Asset> findByAssetIdAndUser(Long assetId, User user);
}
