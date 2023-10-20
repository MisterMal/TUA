package ssbd01.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ssbd01.dto.PatientDataDTO;
import ssbd01.dto.prescrription.PrescriptionDTO;
import ssbd01.entities.OrderState;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private long id;

    @NotNull
    private OrderState orderState;

    @NotNull
    private Date orderDate;

    private PrescriptionDTO prescription;

    private PatientDataDTO patientData;

    private Boolean prescriptionApproved;

    private Boolean patientApproved;

    @NotNull
    private List<OrderMedicationDTO> orderMedication;
}
