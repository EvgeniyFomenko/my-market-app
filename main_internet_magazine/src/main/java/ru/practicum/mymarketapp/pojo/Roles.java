package ru.practicum.mymarketapp.pojo;

public enum Roles {
    ADMIN("ADMIN"),
    BUYER("BUYER"),
    SELLER("SELLER");

    Roles(String name) {
        this.fullName = name;
    }

    public String getFullName() {
        return fullName;
    }

    private String fullName;
}
