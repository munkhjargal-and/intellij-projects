package mn.water.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "box")
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float length;
    private Float width;
    private Float height;

    @Transient
    public Float getVolume() {
        if (length == null || width == null || height == null) {
            return null;
        }
        return length * width * height;
    }
}