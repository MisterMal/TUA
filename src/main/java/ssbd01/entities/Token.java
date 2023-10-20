package ssbd01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "token")
@Setter
@Builder
@AllArgsConstructor
@NamedQuery(name = "token.findAll", query = "SELECT o FROM Token o")
@NamedQuery(name = "token.findByAccountId", query = "SELECT o FROM Token o WHERE o.account.id = ?1")
@NamedQuery(name = "token.findByCode", query = "SELECT o FROM Token o WHERE o.code = ?1")
@NamedQuery(
    name = "token.findByTypeAndBeforeGivenDataAndNotUsedAndNotPreviouslySent",
    query =
        "SELECT o FROM Token o WHERE o.tokenType = ?1 "
            + "AND o.expirationDate < ?2 AND o.isUsed = false AND o.wasPreviousTokenSent = false")
public class Token extends AbstractEntity {

  @Getter
  @Enumerated(EnumType.STRING)
  @Column(name = "token_type", nullable = false)
  TokenType tokenType;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(lombok.AccessLevel.NONE)
  private Long id;

  @Size(min = 8, max = 100)
  @Column(name = "code", nullable = false, updatable = false, unique = true)
  private String code;

  @Column(name = "used", nullable = false, columnDefinition = "boolean default false")
  private boolean isUsed;

  @Column(
      name = "was_previous_token_sent",
      nullable = false,
      columnDefinition = "boolean default false")
  private boolean wasPreviousTokenSent;

  @ManyToOne(optional = false, cascade = CascadeType.MERGE)
  @JoinColumn(name = "account_id", updatable = false, nullable = false)
  private Account account;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expiration_date", nullable = false, updatable = false)
  private Date expirationDate;

  public Token(String code, boolean isUsed, Date expirationDate) {
    this.code = code;
    this.isUsed = isUsed;
    this.expirationDate = expirationDate;
  }
}
