package ru.practicum.mymarketapp.pojo;


public class PageableCaching {
    private int pageNumber;
    private int pageSize;
    private String sort;
    public PageableCaching() {

    }

    public PageableCaching(int pageNumber, int pageSize, String sort) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
    public boolean isPaged() {
        return true;
    }
}
