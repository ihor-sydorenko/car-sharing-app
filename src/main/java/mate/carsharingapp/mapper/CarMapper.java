package mate.carsharingapp.mapper;

import mate.carsharingapp.config.MapperConfig;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import mate.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    @Named("toCarDetailsInfoDto")
    CarDetailsInfoDto toDetailsInfoDto(Car car);

    Car toModel(CreateCarRequestDto requestDto);

    void updateCarFromDto(CreateCarRequestDto requestDto, @MappingTarget Car existingCar);
}
