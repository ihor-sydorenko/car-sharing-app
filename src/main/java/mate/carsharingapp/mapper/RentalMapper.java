package mate.carsharingapp.mapper;

import mate.carsharingapp.config.MapperConfig;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CarMapper.class)
public interface RentalMapper {
    @Mapping(target = "carDetailsInfoDto", source = "car", qualifiedByName = "toCarDetailsInfoDto")
    @Mapping(target = "userId", source = "user.id")
    RentalDto toDto(Rental rental);

    Rental toModel(CreateRentalRequestDto requestDto);
}
