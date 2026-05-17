package com.law.admin.model;

import lombok.Data;

import java.util.List;

/**
 * 標準回應格式
 */
@Data
public class ResultModel<T> {
    private Integer total;
    private Integer page;
    private Integer size;
    private List<T> items;

    public ResultModel(Integer total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public ResultModel(Integer total, Integer page, Integer size, List<T> items) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.items = items;
    }
}