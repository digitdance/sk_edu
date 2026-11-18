package org.springcrazy.common.pojo;


public class PaginateDTO {
    private Integer page;
    private Integer pageSize = 20;
    private String sortBy;
    private String sortType;
    private String courseName;

    public PaginateDTO() {
    }

    public Integer getPage() {
        return this.page;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public String getSortType() {
        return this.sortType;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setSortBy(final String sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortType(final String sortType) {
        this.sortType = sortType;
    }

    public void setCourseName(final String courseName) {
        this.courseName = courseName;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PaginateDTO)) {
            return false;
        } else {
            PaginateDTO other = (PaginateDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$page = this.getPage();
                    Object other$page = other.getPage();
                    if (this$page == null) {
                        if (other$page == null) {
                            break label71;
                        }
                    } else if (this$page.equals(other$page)) {
                        break label71;
                    }

                    return false;
                }

                Object this$pageSize = this.getPageSize();
                Object other$pageSize = other.getPageSize();
                if (this$pageSize == null) {
                    if (other$pageSize != null) {
                        return false;
                    }
                } else if (!this$pageSize.equals(other$pageSize)) {
                    return false;
                }

                label57: {
                    Object this$sortBy = this.getSortBy();
                    Object other$sortBy = other.getSortBy();
                    if (this$sortBy == null) {
                        if (other$sortBy == null) {
                            break label57;
                        }
                    } else if (this$sortBy.equals(other$sortBy)) {
                        break label57;
                    }

                    return false;
                }

                Object this$sortType = this.getSortType();
                Object other$sortType = other.getSortType();
                if (this$sortType == null) {
                    if (other$sortType != null) {
                        return false;
                    }
                } else if (!this$sortType.equals(other$sortType)) {
                    return false;
                }

                Object this$courseName = this.getCourseName();
                Object other$courseName = other.getCourseName();
                if (this$courseName == null) {
                    if (other$courseName == null) {
                        return true;
                    }
                } else if (this$courseName.equals(other$courseName)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PaginateDTO;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $page = this.getPage();
        result = result * 59 + ($page == null ? 43 : $page.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        Object $sortBy = this.getSortBy();
        result = result * 59 + ($sortBy == null ? 43 : $sortBy.hashCode());
        Object $sortType = this.getSortType();
        result = result * 59 + ($sortType == null ? 43 : $sortType.hashCode());
        Object $courseName = this.getCourseName();
        result = result * 59 + ($courseName == null ? 43 : $courseName.hashCode());
        return result;
    }

    public String toString() {
        return "PaginateDTO(page=" + this.getPage() + ", pageSize=" + this.getPageSize() + ", sortBy=" + this.getSortBy() + ", sortType=" + this.getSortType() + ", courseName=" + this.getCourseName() + ")";
    }
}
