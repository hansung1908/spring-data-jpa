package com.hansung.spring_data_jpa.service;

public class SaveRequest {
    private String email;
    private String name;

    public SaveRequest(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
