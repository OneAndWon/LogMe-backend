package com.haru.LogMe.domain.budget.repository;

import com.haru.LogMe.domain.budget.entity.Asset;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByAssetIdAndUser(Long assetId, User user);
}
