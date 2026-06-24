package mn.water.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class WaterBottleDto {
    private Long id;
    private Long vendorId;
    private String brand;
    private Double capacity;
    private String barcode;

}