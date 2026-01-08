package com.haru.LogMe.global.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {
    // 로컬 저장 경로 (사용자 홈 디렉토리 하위 logme_images 폴더)
    // 윈도우: C:\Users\사용자\logme_images
    // 맥/리눅스: /Users/사용자/logme_images
    private final String rootPath = System.getProperty("user.home") + File.separator + "logme_images" + File.separator;

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;

        File directory = new File(rootPath);
        if (!directory.exists()) directory.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String storeFileName = UUID.randomUUID() + "_" + originalFilename;
        String fullPath = rootPath + storeFileName;

        file.transferTo(new File(fullPath));
        return fullPath; // 저장된 파일 경로 반환
    }

    public void deleteFile(String filePath) {
        if (filePath == null) return;

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
