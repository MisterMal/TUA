package ssbd01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@Table(name = "admin_data")
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@ToString(callSuper = true)
@NamedQuery(name = "adminData.findAll", query = "SELECT o FROM AdminData o")
public class AdminData extends AccessLevel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(nullable = false, name = "work_phone_number")
  @NotNull
  @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
  private String workPhoneNumber;

  @Builder
  public AdminData(Long id, String workPhoneNumber) {
    super(id, Role.ADMIN);
    this.workPhoneNumber = workPhoneNumber;
  }
}
