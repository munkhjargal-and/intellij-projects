package mn.water.dto;

import java.util.List;

public class SomeDto<E> {
    private int page;
    private int pageSize;
    private int total;
    private List<E> data;


    public SomeDto(int page, int pageSize, int total, List<E> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }
}
