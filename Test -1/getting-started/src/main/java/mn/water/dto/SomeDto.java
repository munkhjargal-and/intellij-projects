package mn.water.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter @Setter @NoArgsConstructor
public class SomeDto<E> {
    @Min(value = 0, message = "page value must be 0 or greater")
    private int page;

    @Min(value = 1, message = "pageSize value must be 1 or greater")
    private int pageSize;

    @Min(value = 0, message = "total value must be 0 or greater")
    private int total;
    private List<E> data;


    public SomeDto(int page, int pageSize, int total, List<E> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }
}
