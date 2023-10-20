package ssbd01.dto.shipment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssbd01.common.SignableEntity;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MedicationCreateShipmentDTO implements SignableEntity {
    @NotNull
    private String name;

    @NotNull
    private Long version;

    @NotNull
    private String etag;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @Override
    public String getSignablePayload() {
        return String.format("%s.%d", name, version);
    }
}
