package ssbd01.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(
    name = "shipment",
    indexes = {
      @Index(name = "shipment_index", columnList = "id", unique = true),
    })
@NamedQuery(
        name = "Shipment.findAllNotProcessed",
        query = "SELECT s FROM Shipment s " +
                "left join ShipmentMedication sm " +
                "where sm.processed = false")
public class Shipment extends AbstractEntity implements Serializable {

  public static final long serialVersionUID = 1L;

  @Builder(builderMethodName = "createBuilder")
  public Shipment(Date shipmentDate,
                  List<ShipmentMedication> shipmentMedications) {
    this.shipmentDate = shipmentDate;
    this.shipmentMedications = new ArrayList<>(shipmentMedications);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(lombok.AccessLevel.NONE)
  private Long id;

  @Column(nullable = false, name = "shipment_date", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date shipmentDate;

  @OneToMany(
      mappedBy = "shipment",
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH},
      orphanRemoval = true)
  private List<ShipmentMedication> shipmentMedications = new ArrayList<>();
}
