package mate.carsharingapp.service.rental;

import java.util.List;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.dto.rental.RentalSearchParametersDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalDto createRental(CreateRentalRequestDto requestDto, Authentication authentication);

    List<RentalDto> searchByManager(RentalSearchParametersDto parametersDto, Pageable pageable);

    List<RentalDto> getRentalsByUserId(Boolean isActive,
                                       Pageable pageable,
                                       Authentication authentication);

    RentalDto getRentalById(Long rentalId, Authentication authentication);

    RentalDto setActualReturnDate(Long rentalId);
}
