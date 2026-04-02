package ru.practicum.mymarketapp.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.mymarketapp.pojo.VariableSort;

public class PagableUtil {
    public static Pageable getPageable(int pageNumber, int pageSize, String sort){
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);;

        if (VariableSort.ALPHA.getFullName().equals(sort)){
            pageable =  PageRequest.of(pageNumber-1, pageSize, Sort.by("title"));
        } else if  (VariableSort.PRICE.getFullName().equals(sort)) {
            pageable  = PageRequest.of(pageNumber-1, pageSize,Sort.by("price"));
        }
        return pageable;
    }
}
