package mn.water.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter @Setter @NoArgsConstructor
public class VendorDto {
    private Long id;
    private String name;
    private Long registrationNumber;
    private Date contractSignedDate;
    private Date contractEndDate;

}