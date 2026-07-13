package ru.practicum.mymarketapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "user_role")
public class UserRole {
    @Id
    private Long id;
    private Long userId;
    private String role;

    public UserRole() {
    }

    public UserRole(Long id, Long userId, String roleId) {
        this.id = id;
        this.userId = userId;
        this.role = roleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRoleId(String role) {
        this.role = role;
    }
}
