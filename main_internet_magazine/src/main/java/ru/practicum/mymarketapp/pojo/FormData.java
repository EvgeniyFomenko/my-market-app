package ru.practicum.mymarketapp.pojo;

public class FormData {
    String id;
    String search;
    String sort;
    String pageSize;
    String pageNumber;
    String action;

    public FormData(String id, String search, String sort, String pageSize, String pageNumber, String action) {
        this.id = id;
        this.search = search;
        this.sort = sort;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
