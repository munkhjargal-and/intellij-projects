package mn.water.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter @Setter @NoArgsConstructor
public class VendorDto {
    private Long id;

    @NotBlank(message = "name may not be blank")
    private String name;

    @Min(value = 0, message = "registration value must be 0 or greater")
    private Long registrationNumber;
    private Date contractSignedDate;
    private Date contractEndDate;

}