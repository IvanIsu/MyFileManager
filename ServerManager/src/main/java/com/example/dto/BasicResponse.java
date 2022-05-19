package com.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data public class BasicResponse implements Serializable {


    private String response;

    public BasicResponse(String response) {
        this.response = response;
    }
}
