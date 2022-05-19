package com.example.dto;

public class FileDownloadResponse extends BasicResponse {

    private String fileName;
    private String dstPath;
    private byte[] bytes;

    public FileDownloadResponse(String response, String fileName, String dstPath, byte[] bytes) {
        super(response);
        this.fileName = fileName;
        this.dstPath = dstPath;
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDstPath() {
        return dstPath;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
