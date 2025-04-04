package mate.carsharingapp.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotBlank
    private String type;
    @NotNull
    @Positive
    private int inventory;
    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
