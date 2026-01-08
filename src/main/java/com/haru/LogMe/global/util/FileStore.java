package com.haru.LogMe.global.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStore {
    String storeFile(MultipartFile file) throws IOException;

    void deleteFile(String filePath);
}
