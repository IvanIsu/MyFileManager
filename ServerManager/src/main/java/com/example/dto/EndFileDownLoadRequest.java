package com.example.dto;

import lombok.Data;

@Data public class EndFileDownLoadRequest implements BasicRequest{
    private String fileName;

    public EndFileDownLoadRequest(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getType() {
        return null;
    }
}
