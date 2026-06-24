package mn.water.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter @Setter @NoArgsConstructor
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
}
