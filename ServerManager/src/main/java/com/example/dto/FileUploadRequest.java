package com.example.dto;

import lombok.Data;

@Data public class FileUploadRequest implements BasicRequest {
    private String srcPath;
    private String dstPath;
    private String fileName;

    public FileUploadRequest(String srcPath, String dstPath, String fileName) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.fileName = fileName;
    }

    @Override
    public String getType() {
        return null;
    }
}