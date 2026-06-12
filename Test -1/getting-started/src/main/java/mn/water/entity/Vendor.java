package mn.water.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "vendor")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long registrationNumber;
    private Date contractSignedDate;
    private Date getContractEndDate;

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

    public Date getGetContractEndDate() {
        return getContractEndDate;
    }

    public void setGetContractEndDate(Date getContractEndDate) {
        this.getContractEndDate = getContractEndDate;
    }
}