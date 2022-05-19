package com.example.dto;

import lombok.Data;

@Data public class GetFileListRequest implements BasicRequest {

    private String fileName;
    private String path;

    public GetFileListRequest(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
    }

    @Override
    public String getType() {
        return null;
    }
}
