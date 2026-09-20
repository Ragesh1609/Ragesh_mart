package com.ragesh.rageshmart.dto;

import com.ragesh.rageshmart.model.User;

/** Section 13, rule 4: DTOs are separate from entities; this must never carry passwordHash. */
public class UserResponseDTO {
    private final Long id;
    private final String name;
    private final String email;
    private final String role;

    private UserResponseDTO(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
