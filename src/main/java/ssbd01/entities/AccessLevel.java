package ssbd01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;

@Entity
@Table(name = "access_level", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"access_level_role", "account_id"})
})
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorColumn(name = "access_level_role", discriminatorType = DiscriminatorType.STRING)
public abstract class AccessLevel extends AbstractEntity implements Serializable, CharSequence {

  public static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(lombok.AccessLevel.NONE)
  private Long id;

  @Getter
  @Enumerated(EnumType.STRING)
  @Setter(lombok.AccessLevel.NONE)
  @Column(name = "access_level_role", insertable = false, nullable = false, updatable = false)
  private Role role;

  @Column(nullable = false, columnDefinition = "boolean default true")
  @NotNull
  private Boolean active = true;

  @ManyToOne(optional = false)
  @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false)
  private Account account;

  public AccessLevel(Long id, Role role) {
    this.id = id;
    this.role = role;
  }

  @Override
  public int length() {
    // Implement the length method based on the properties of the AccessLevel class
    // Return an appropriate length value based on your needs.
    return Long.toString(id).length() + role.toString().length() + (active ? "true" : "false").length();
  }

  @Override
  public char charAt(int index) {
    // Implement the charAt method based on the concatenated string of relevant properties
    String accessLevelString = Long.toString(id) + role.toString() + (active ? "true" : "false");
    return accessLevelString.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    // Implement the subSequence method based on the concatenated string of relevant properties
    String accessLevelString = Long.toString(id) + role.toString() + (active ? "true" : "false");
    return accessLevelString.subSequence(start, end);
  }
}
