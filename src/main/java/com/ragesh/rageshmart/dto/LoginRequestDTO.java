package com.ragesh.rageshmart.dto;

/** Shape of the JSON body for POST /api/v1/auth/login. */
public class LoginRequestDTO {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
