package com.example.dto;

import lombok.Data;

@Data public class RegRequest implements BasicRequest {

    private String login;
    private int password;
    private String nickName;

    public RegRequest(String login, String nickName, int password) {
        this.login = login;
        this.password = password;
        this.nickName = nickName;
    }

    @Override
    public String getType() {
        return null;
    }
}
