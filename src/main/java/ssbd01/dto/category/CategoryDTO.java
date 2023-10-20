package ssbd01.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTO {

    @NotNull
    @Size(max = 50, min = 2)
    private String name;

    //@NotNull
    private Boolean isOnPrescription;

    @Builder
    public CategoryDTO(String name, Boolean isOnPrescription) {
        this.name = name;
        this.isOnPrescription = isOnPrescription;
    }
}
