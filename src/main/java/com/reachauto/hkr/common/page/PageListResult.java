package com.reachauto.hkr.common.page;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/6/8.
 */
public class PageListResult<T> {

    private static final PageListResult EMPTY_RESULT = new PageListResult();

    // 当前页的索引值，起始值为0
    protected final int currentPage;
    // 总页数
    protected final int totalPages;
    // 数据总数
    protected final int totalCount;
    // 当前页记录列表
    protected List<T> list = Collections.emptyList();

    public PageListResult() {
        this.currentPage = 0;
        this.totalPages = 0;
        this.totalCount = 0;
    }

    public PageListResult(int totalCount, List list) {
        this.currentPage = 0;
        this.totalPages = 0;
        this.totalCount = totalCount;
        this.list = list == null ? Collections.emptyList() : list;
    }

    public PageListResult(PageSortCondition condition, int totalCount, List list) {

        this.totalCount = totalCount;
        this.list = list == null ? Collections.emptyList() : list;

        if (condition == null) {
            this.currentPage = 0;
            this.totalPages = 0;
        } else {
            if (condition.getCurrentPage() == null || condition.getCurrentPage() < 0) {
                this.currentPage = 0;
            } else {
                this.currentPage = condition.getCurrentPage();
            }
            if (condition.getPageSize() == null || condition.getPageSize() < 1) {
                this.totalPages = 0;
            } else {
                this.totalPages = totalCount / condition.getPageSize() + (totalCount % condition.getPageSize() > 0 ? 1 : 0);
            }
        }

    }

    public static <T> PageListResult<T> emptyResult() {
        return (PageListResult<T>) EMPTY_RESULT;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "PageListResult{" +
                "currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", totalCount=" + totalCount +
                ", list=" + list +
                '}';
    }

}
