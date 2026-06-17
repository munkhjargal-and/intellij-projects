package mn.water.dto;

import mn.water.entity.Box;

import java.util.List;

public class BoxSomeDto {
    private int page;
    private int pageSize;
    private int total;
    private List<Box> data;

    public BoxSomeDto(int page, int pageSize, int total, List<Box> data) {
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

    public List<Box> getData() {
        return data;
    }

    public void setData(List<Box> data) {
        this.data = data;
    }
}
