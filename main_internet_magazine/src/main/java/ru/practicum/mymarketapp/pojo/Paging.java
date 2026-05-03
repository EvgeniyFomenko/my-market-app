package ru.practicum.mymarketapp.pojo;

public class Paging {
    boolean hasNext;
    boolean hasPrevious;
    int pageNumber;
    int pageSize;

    public Paging(boolean hasNext, boolean hasPrevious, int pageNumber, int pageSize) {
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int pageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
