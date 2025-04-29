package mate.carsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;

@Accessors(chain = true)
@Data
public class RentalDto {
    private Long id;
    private LocalDate rentalDate = LocalDate.now();
    private LocalDate returnDate;
    private CarDetailsInfoDto carDetailsInfoDto;
    private Long userId;
}
