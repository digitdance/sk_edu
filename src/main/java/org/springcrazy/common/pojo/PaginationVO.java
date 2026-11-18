package org.springcrazy.common.pojo;

import java.util.List;

public class PaginationVO {
    private List<?> records;
    private Long total;
    private Long page;
    private Long pageSize;
    private Long pageCount;

    public PaginationVO() {
    }

    public List<?> getRecords() {
        return this.records;
    }

    public Long getTotal() {
        return this.total;
    }

    public Long getPage() {
        return this.page;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    public Long getPageCount() {
        return this.pageCount;
    }

    public void setRecords(final List<?> records) {
        this.records = records;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }

    public void setPage(final Long page) {
        this.page = page;
    }

    public void setPageSize(final Long pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageCount(final Long pageCount) {
        this.pageCount = pageCount;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PaginationVO)) {
            return false;
        } else {
            PaginationVO other = (PaginationVO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$total = this.getTotal();
                    Object other$total = other.getTotal();
                    if (this$total == null) {
                        if (other$total == null) {
                            break label71;
                        }
                    } else if (this$total.equals(other$total)) {
                        break label71;
                    }

                    return false;
                }

                Object this$page = this.getPage();
                Object other$page = other.getPage();
                if (this$page == null) {
                    if (other$page != null) {
                        return false;
                    }
                } else if (!this$page.equals(other$page)) {
                    return false;
                }

                label57: {
                    Object this$pageSize = this.getPageSize();
                    Object other$pageSize = other.getPageSize();
                    if (this$pageSize == null) {
                        if (other$pageSize == null) {
                            break label57;
                        }
                    } else if (this$pageSize.equals(other$pageSize)) {
                        break label57;
                    }

                    return false;
                }

                Object this$pageCount = this.getPageCount();
                Object other$pageCount = other.getPageCount();
                if (this$pageCount == null) {
                    if (other$pageCount != null) {
                        return false;
                    }
                } else if (!this$pageCount.equals(other$pageCount)) {
                    return false;
                }

                Object this$records = this.getRecords();
                Object other$records = other.getRecords();
                if (this$records == null) {
                    if (other$records == null) {
                        return true;
                    }
                } else if (this$records.equals(other$records)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PaginationVO;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : $total.hashCode());
        Object $page = this.getPage();
        result = result * 59 + ($page == null ? 43 : $page.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        Object $pageCount = this.getPageCount();
        result = result * 59 + ($pageCount == null ? 43 : $pageCount.hashCode());
        Object $records = this.getRecords();
        result = result * 59 + ($records == null ? 43 : $records.hashCode());
        return result;
    }

    public String toString() {
        return "PaginationVO(records=" + this.getRecords() + ", total=" + this.getTotal() + ", page=" + this.getPage() + ", pageSize=" + this.getPageSize() + ", pageCount=" + this.getPageCount() + ")";
    }
}
