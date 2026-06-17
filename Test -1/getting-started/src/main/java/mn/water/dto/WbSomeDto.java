package mn.water.dto;

import mn.water.entity.WaterBottle;

import java.util.List;

public class WbSomeDto {
    private int page;
    private int pageSize;
    private int total;
    private List<WaterBottle> data;

    public WbSomeDto(int page, int pageSize, int total, List<WaterBottle> data) {
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

    public List<WaterBottle> getData() {
        return data;
    }

    public void setData(List<WaterBottle> data) {
        this.data = data;
    }
}
