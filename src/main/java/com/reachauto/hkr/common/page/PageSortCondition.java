package com.reachauto.hkr.common.page;

/**
 * Created by Administrator on 2017/6/5.
 */
public class PageSortCondition {

    public static final String NAME = "condition";

    protected static final int CURRENT_PAGE_DEFAULT = 0;

    protected static final int PAGE_SIZE_DEFAULT = 10;

    // 当前页的索引值，默认值为空
    protected Integer currentPage;

    // 每页显示的数据数
    protected Integer pageSize;

    // 排序信息
    protected SortOrder sortOrder = new SortOrder();

    public PageSortCondition() {
        // 默认当前页索引值和每页数据数为空
    }

    public PageSortCondition(Integer currentPage, Integer pageSize) {
        if (currentPage == null || pageSize == null) {
            return;
        }
        this.currentPage = currentPage < 0 ? CURRENT_PAGE_DEFAULT : currentPage;
        this.pageSize = pageSize < 1 ? PAGE_SIZE_DEFAULT : pageSize;
    }

    public PageSortCondition(Integer currentPage, Integer pageSize, SortOrder sortOrder) {
        if (currentPage == null || pageSize == null) {
            return;
        }
        this.currentPage = currentPage < 0 ? CURRENT_PAGE_DEFAULT : currentPage;
        this.pageSize = pageSize < 1 ? PAGE_SIZE_DEFAULT : pageSize;
        this.sortOrder = sortOrder;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        if (currentPage == null) {
            return;
        }
        this.currentPage = currentPage < 0 ? CURRENT_PAGE_DEFAULT : currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null) {
            return;
        }
        this.pageSize = pageSize < 1 ? PAGE_SIZE_DEFAULT : pageSize;
    }

    public Integer getOffset() {

        if (this.currentPage == null || this.pageSize == null) {
            return null;
        }
        return this.currentPage * this.pageSize;

    }

    public Integer getLimit() {
        return this.pageSize;
    }

    public PageSortCondition addAscOrder(String orderField) {
        this.sortOrder.addAscOrder(orderField);
        return this;
    }

    public PageSortCondition addDescOrder(String orderField) {
        this.sortOrder.addDescOrder(orderField);
        return this;
    }

    public String getOrderContent() {
        return this.sortOrder.getOrderContent();
    }

    @Override
    public String toString() {
        return "PageSortCondition{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", sortOrder=" + sortOrder +
                '}';
    }

}
