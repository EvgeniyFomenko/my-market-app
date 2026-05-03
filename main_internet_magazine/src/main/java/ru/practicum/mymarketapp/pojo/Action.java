package ru.practicum.mymarketapp.pojo;

public enum Action {
    PLUS("PLUS"),
    MINUS("MINUS"),
    DELETE("DELETE");

    Action(String name) {
        this.fullName = name;
    }

    public String getFullName() {
        return fullName;
    }

    private String fullName;
}
