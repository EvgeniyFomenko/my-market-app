package ru.practicum.mymarketapp.pojo;

public enum VariableSort {

    NO("NO"),
    ALPHA("ALPHA"),
    PRICE("PRICE");

     VariableSort(String name) {
        this.fullName = name;
    }

    public String getFullName() {
         return fullName;
    }

    private String fullName;
}
