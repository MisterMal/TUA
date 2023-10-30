package ssbd01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.util.stream.IntStream;

@Entity
@Getter
@Table(name = "chemist_data")
@Setter
@DiscriminatorValue("CHEMIST")
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@NamedQuery(name = "chemistData.findAll", query = "SELECT o FROM ChemistData o")
public class ChemistData extends AccessLevel implements Serializable, CharSequence{

  private static final long serialVersionUID = 1L;

  @Builder
  public ChemistData(Long id, String licenseNumber) {
    super(id, Role.CHEMIST);
    this.licenseNumber = licenseNumber;
  }

  @NotNull
  @Pattern(regexp = "^\\d{6}$", message = "Invalid license number")
  @Column(nullable = false, unique = true, name = "license_number")
  private String licenseNumber;

  @Override
  public int length() {
    return licenseNumber.length();
  }

  @Override
  public char charAt(int index) {
    return licenseNumber.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return licenseNumber.subSequence(start, end);
  }
}
