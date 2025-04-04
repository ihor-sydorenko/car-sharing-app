package mate.carsharingapp.service.car;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.mapper.CarMapper;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.repository.CarRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDetailsInfoDto save(CreateCarRequestDto requestDto) {
        checkCarTypeIsValid(requestDto.getType());
        Car car = carRepository.save(carMapper.toModel(requestDto));
        return carMapper.toDetailsInfoDto(car);
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDetailsInfoDto findById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find car by id: %s", id))
        );
        return carMapper.toDetailsInfoDto(car);
    }

    @Override
    public CarDetailsInfoDto updateById(CreateCarRequestDto requestDto, Long id) {
        Car existingCar = carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find car by id: %s", id))
        );
        checkCarTypeIsValid(requestDto.getType());
        carMapper.updateCarFromDto(requestDto, existingCar);
        return carMapper.toDetailsInfoDto(existingCar);
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    private void checkCarTypeIsValid(String carType) {
        boolean typeIsValid = Arrays.stream(Car.CarType.values())
                .anyMatch(type -> type.name().equalsIgnoreCase(carType));
        if (!typeIsValid) {
            throw new IllegalArgumentException(
                    "Incorrect car type. Cart type can be only: SEDAN, SUV, HATCHBACK, UNIVERSAL");
        }
    }
}
