package ssbd01.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "prescription",
    indexes = {
      @Index(
          name = "patient_data_index_perscription",
          columnList = "patient_data_id")
    },
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"patient_data_id", "prescription_number"}),
    })
public class Prescription extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(lombok.AccessLevel.NONE)
  private Long id;

  @ManyToOne(
      optional = false,
      cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "patient_data_id", nullable = false, updatable = false)
  private AccessLevel patientData;

  @Column(nullable = false, name = "prescription_number")
  private String prescriptionNumber;

  @Builder
  public Prescription(String prescriptionNumber) {
    this.prescriptionNumber = prescriptionNumber;
  }
}
