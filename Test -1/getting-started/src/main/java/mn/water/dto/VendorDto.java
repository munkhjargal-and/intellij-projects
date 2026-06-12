package mn.water.dto;

import java.util.Date;

public class VendorDto {
    private Long id;
    private String name;
    private Long registrationNumber;
    private Date contractSignedDate;
    private Date contractEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Date getContractSignedDate() {
        return contractSignedDate;
    }

    public void setContractSignedDate(Date contractSignedDate) {
        this.contractSignedDate = contractSignedDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }
}