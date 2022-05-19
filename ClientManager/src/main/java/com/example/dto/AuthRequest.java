package com.example.dto;

import lombok.Data;

@Data public class AuthRequest implements BasicRequest {

    private String login;
    private int password;

    public AuthRequest(String login, int password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String getType() {
        return "authOk";
    }

}
