package ru.practicum.mymarketapp.pojo;

import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.mymarketapp.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageCaching {
    private PageableCaching pageable;
    private List<Item> content;
    private Long total;


    public PageCaching() {
        pageable = new PageableCaching();
        content = new ArrayList<>();
    }

    public PageCaching(List<Item> content) {
       this.content=content;
    }

//    public PageCaching(List content, Pageable pageable, Long total) {
//        this.content=content;
//        this.pageable= new PageableCaching(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
//        this.total = total;
//    }

    public PageCaching(List<Item> content, PageableCaching pageable, Long total) {
        this.content=content;
        this.pageable= pageable;
        this.total = total;
    }
    public void setContent(List<Item> content) {
        this.content = content;
    }
    public void setPageable(PageableCaching pageable) {
        this.pageable = pageable;
    }
    public void setPageSize(Long pageSize) {
        total = pageSize;
    }

    public PageableCaching getPageable() {
        return pageable;
    }

    public List<Item> getContent() {
        return content;
    }

    public Long getTotal() {
        return total;
    }

    public int getNumber() {
        return pageable.isPaged() ? pageable.getPageNumber() : 0;
    }

    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    public int getSize() {
        return pageable.isPaged() ? pageable.getPageSize() : content.size();
    }
    public boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }

    public boolean hasPrevious() {
        return getNumber() > 0;
    }

}
