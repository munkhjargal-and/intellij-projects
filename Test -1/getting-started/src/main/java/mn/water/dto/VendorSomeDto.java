package mn.water.dto;

import mn.water.entity.Vendor;

import java.util.List;

public class VendorSomeDto {
    private int page;
    private int pageSize;
    private int total;
    private List<Vendor> data;

    public VendorSomeDto(int page, int pageSize, int total, List<Vendor> data) {
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

    public List<Vendor> getData() {
        return data;
    }

    public void setData(List<Vendor> data) {
        this.data = data;
    }
}
