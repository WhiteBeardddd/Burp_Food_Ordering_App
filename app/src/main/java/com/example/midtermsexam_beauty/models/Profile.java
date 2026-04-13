package com.example.midtermsexam_beauty.models;

public class Profile {
    private String id;
    private String authId;
    private String fullName;
    private boolean isSeller;
    private String phone;
    private String avatarUrl;

    // Getters
    public String getId() { return id; }
    public String getAuthId() { return authId; }
    public String getFullName() { return fullName; }
    public boolean isSeller() { return isSeller; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setAuthId(String authId) { this.authId = authId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setSeller(boolean seller) { this.isSeller = seller; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
