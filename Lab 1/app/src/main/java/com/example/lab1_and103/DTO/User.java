package com.example.lab1_and103.DTO;

import androidx.annotation.NonNull;

public class User {
    private String documentId;
    private String maUser;
    private String tenUser;
    private String namSinh;

    public User(String documentId, String maUser, String tenUser, String namSinh) {
        this.documentId = documentId;
        this.maUser = maUser;
        this.tenUser = tenUser;
        this.namSinh = namSinh;
    }

    public User() {
    }

    public String getMaUser() {
        return maUser;
    }

    public void setMaUser(String maUser) {
        this.maUser = maUser;
    }

    public String getTenUser() {
        return tenUser;
    }

    public void setTenUser(String tenUser) {
        this.tenUser = tenUser;
    }

    public String getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(String namSinh) {
        this.namSinh = namSinh;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
