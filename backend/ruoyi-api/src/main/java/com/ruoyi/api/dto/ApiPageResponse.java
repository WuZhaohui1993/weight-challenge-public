package com.ruoyi.api.dto;

import com.github.pagehelper.PageInfo;

import java.util.Collections;
import java.util.List;

/**
 * 移动端分页响应
 */
public class ApiPageResponse<T> {

    private List<T> list;

    private long total;

    private int pageNum;

    private int pageSize;

    public static <T> ApiPageResponse<T> fromList(PageInfo<T> pageInfo) {
        ApiPageResponse<T> response = new ApiPageResponse<>();
        response.setList(pageInfo.getList() != null ? pageInfo.getList() : Collections.<T>emptyList());
        response.setTotal(pageInfo.getTotal());
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        return response;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
