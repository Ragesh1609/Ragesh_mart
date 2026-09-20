package com.ragesh.rageshmart.dto;

/** Shape of the JSON body for POST /api/v1/auth/register. */
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private String role; // "BUYER" or "SELLER" only - ADMIN is seed-only (F1)

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
