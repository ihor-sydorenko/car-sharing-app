package mate.carsharingapp.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotBlank
    private String type;
    @Positive
    private int inventory;
    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
