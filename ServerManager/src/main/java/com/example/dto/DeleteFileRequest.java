package com.example.dto;

import lombok.Data;

@Data public class DeleteFileRequest implements BasicRequest{

    private String path;

    public DeleteFileRequest(String path) {
        this.path = path;
    }

    @Override
    public String getType() {
        return null;
    }
}
