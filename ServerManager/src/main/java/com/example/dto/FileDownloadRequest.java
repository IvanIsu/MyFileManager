package com.example.dto;

import lombok.Data;

@Data
public class FileDownloadRequest implements BasicRequest {

    String fileName;
    String dstPath;
    byte[] bytes;




    public FileDownloadRequest(String fileName, String dstPath, byte[] bytes) {
        this.bytes = bytes;
        this.fileName = fileName;
        this.dstPath = dstPath;
    }

    @Override
    public String getType() {
        return "File";
    }
}
