package mate.carsharingapp.service.car;

import java.util.List;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDetailsInfoDto save(CreateCarRequestDto requestDto);

    List<CarDto> findAll(Pageable pageable);

    CarDetailsInfoDto findById(Long id);

    CarDetailsInfoDto updateById(CreateCarRequestDto requestDto, Long id);

    void deleteById(Long id);
}
