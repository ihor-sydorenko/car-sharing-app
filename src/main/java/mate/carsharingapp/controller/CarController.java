package mate.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import mate.carsharingapp.service.car.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Create new car", description = "Add new car in database")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CarDetailsInfoDto addCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Get all cars", description = "Get a list of all available cars")
    @GetMapping
    public List<CarDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @Operation(summary = "Get specific car by id", description = "Get car's detailed information")
    @GetMapping("/{carId}")
    public CarDetailsInfoDto getCarById(@PathVariable Long carId) {
        return carService.findById(carId);
    }

    @Operation(summary = "Update car by id", description = "Update car info by id")
    @PutMapping("/{carId}")
    public CarDetailsInfoDto updateCarInfo(@RequestBody @Valid CreateCarRequestDto requestDto,
                                           @PathVariable Long carId) {
        return carService.updateById(requestDto, carId);
    }

    @Operation(summary = "Delete car by id", description = "Delete car by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{carId}")
    public void deleteCarById(@PathVariable Long carId) {
        carService.deleteById(carId);
    }
}
