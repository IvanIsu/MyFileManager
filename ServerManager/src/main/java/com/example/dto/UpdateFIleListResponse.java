package com.example.dto;

import com.example.FileInfo;


import java.nio.file.Path;
import java.util.List;

public class UpdateFIleListResponse extends BasicResponse {


    private List <FileInfo> fileInfoList;
    private String path;

    public UpdateFIleListResponse(String response, String path, List<FileInfo> fileInfoList) {
        super(response);
        this.path = path;
        this.fileInfoList = fileInfoList;

    }

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

    public String getPath() {
        return path;
    }
}
