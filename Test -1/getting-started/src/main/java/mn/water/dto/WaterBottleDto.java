package mn.water.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class WaterBottleDto {
    private Long id;

    private Long vendorId;

    @NotBlank(message = "brand may not be blank")
    private String brand;

    @Min(value = 0, message = "page value must be 0 or greater")
    private Double capacity;

    @NotBlank(message = "barcode may not be blank")
    private String barcode;

}