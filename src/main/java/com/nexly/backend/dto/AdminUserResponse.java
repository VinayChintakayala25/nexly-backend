package com.nexly.backend.dto;

public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private boolean active;

    public AdminUserResponse(
            Long id,
            String name,
            String email,
            String role,
            String phone,
            boolean active) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return active;
    }
}