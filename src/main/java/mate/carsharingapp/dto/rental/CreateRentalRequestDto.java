package mate.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    private LocalDate rentalDate;
    private LocalDate returnDate;
    @NotNull
    @Positive
    private Long carId;
}
