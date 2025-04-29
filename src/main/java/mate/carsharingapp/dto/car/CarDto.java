package mate.carsharingapp.dto.car;

import lombok.Data;
import lombok.experimental.Accessors;
import mate.carsharingapp.model.Car;

@Accessors(chain = true)
@Data
public class CarDto {
    private String model;
    private String brand;
    private Car.CarType type;
}
