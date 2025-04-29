package mate.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.carsharingapp.model.Car;

@Accessors(chain = true)
@Data
public class CarDetailsInfoDto {
    private Long id;
    private String model;
    private String brand;
    private Car.CarType type;
    private int inventory;
    private BigDecimal dailyFee;
}
