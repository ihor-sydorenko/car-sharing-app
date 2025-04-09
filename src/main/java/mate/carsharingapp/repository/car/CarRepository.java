package mate.carsharingapp.repository.car;

import mate.carsharingapp.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

}
