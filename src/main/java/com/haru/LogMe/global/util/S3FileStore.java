package com.haru.LogMe.global.util;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class S3FileStore implements FileStore {
    private final S3Template s3Template; // Spring Cloud AWS가 제공하는 템플릿

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String storeFileName = UUID.randomUUID() + "_" + originalFilename;

        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(bucketName, storeFileName, inputStream);
        }

        // S3 URL 반환
        return s3Template.download(bucketName, storeFileName).getURL().toString();
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null) return;
        // URL에서 파일명만 추출해서 삭제 (로직 구현 필요)
        // 예: https://버킷.s3.../파일명.jpg -> 파일명.jpg 추출
        String fileName = extractFileNameFromUrl(fileUrl);
        s3Template.deleteObject(bucketName, fileName);
    }

    private String extractFileNameFromUrl(String url) {
        // 간단한 예시 로직 (실제 URL 구조에 맞춰 수정 필요)
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
