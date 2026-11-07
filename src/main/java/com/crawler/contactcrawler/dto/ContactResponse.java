package com.crawler.contactcrawler.dto;

import java.time.LocalDateTime;

public class ContactResponse {
    private Long id;
    private String sourceUrl;
    private String phone;
    private String email;
    private String address;
    private String companyName;
    private LocalDateTime createdAt;

    public ContactResponse() {}

    public ContactResponse(Long id, String sourceUrl, String phone, String email, String address, String companyName, LocalDateTime createdAt) {
        this.id = id;
        this.sourceUrl = sourceUrl;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.companyName = companyName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}