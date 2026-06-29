package mn.water.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class BoxDto {

    private Long id;

    @Min(value = 0, message = "length value must be 0 or greater")
    private Float length;

    @Min(value = 0, message = "width value must be 0 or greater")
    private Float width;

    @Min(value = 0, message = "height value must be 0 or greater")
    private Float height;

}