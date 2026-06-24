package mn.water.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "vendor")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vendor")
    private List<WaterBottle> bottle = new ArrayList<>();

    public Vendor(Long id, String name){
        super();
        this.id = id;
        this.name = name;
    }

    private Long registrationNumber;
    private Date contractSignedDate;
    private Date getContractEndDate;
    }