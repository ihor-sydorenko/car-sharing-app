package mate.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.dto.rental.RentalSearchParametersDto;
import mate.carsharingapp.service.rental.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Create a new rental", description = "Create a new rental by customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping
    public RentalDto createRental(@RequestBody @Valid CreateRentalRequestDto requestDto,
                                  Authentication authentication) {
        return rentalService.createRental(requestDto, authentication);
    }

    @Operation(summary = "Search users rentals", description = "Search user rentals by parameters")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/search")
    public List<RentalDto> getRentalBySearchParameters(RentalSearchParametersDto parametersDto,
                                                       Pageable pageable) {
        return rentalService.searchByManager(parametersDto, pageable);
    }

    @Operation(summary = "Get rental by user id", description = "Get rental by user id")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping
    public List<RentalDto> getRentalByUserId(@RequestParam(required = false) Boolean isActive,
                                             Pageable pageable,
                                             Authentication authentication) {
        return rentalService.getRentalsByUserId(isActive, pageable, authentication);
    }

    @Operation(summary = "Get specific rental by id", description = "Get specific rental by id")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/{rentalId}")
    public RentalDto getRentalById(@PathVariable Long rentalId,
                                   Authentication authentication) {
        return rentalService.getRentalById(rentalId, authentication);
    }

    @Operation(summary = "Returning a rental by id", description = "Set actual return date")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/{rentalId}/return")
    public RentalDto setActualReturnDate(@PathVariable Long rentalId) {
        return rentalService.setActualReturnDate(rentalId);
    }
}
