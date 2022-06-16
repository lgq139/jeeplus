package com.jeeplus.common.json;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AjaxPage<T> implements Serializable {

    private long size;

    private long current;

    private long total;

    private List<T> records = new ArrayList<>();

    public AjaxPage() {

    }

    public AjaxPage(IPage<T> page) {
        this.current = page.getCurrent();
        this.size = page.getSize();
        this.total = page.getTotal();
        this.records = page.getRecords();
    }

    public AjaxPage(long current, long size, long total, List<T> records) {
        this.size = size;
        this.current = current;
        this.total = total;
        this.records = records;
    }

    public long getSize() {
        return size;
    }

    public AjaxPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public long getCurrent() {
        return current;
    }

    public AjaxPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public AjaxPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getRecords() {
        return records;
    }

    public AjaxPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }
}
