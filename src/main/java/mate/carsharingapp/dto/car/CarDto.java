package mate.carsharingapp.dto.car;

import lombok.Data;
import mate.carsharingapp.model.Car;

@Data
public class CarDto {
    private String model;
    private String brand;
    private Car.CarType type;
}
