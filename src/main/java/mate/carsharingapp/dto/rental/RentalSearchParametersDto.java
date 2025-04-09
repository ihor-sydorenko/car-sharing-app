package mate.carsharingapp.dto.rental;

public record RentalSearchParametersDto(Long[] user_ids, Boolean[] is_active) {
}
