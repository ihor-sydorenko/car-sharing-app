package mate.carsharingapp.repository;

import mate.carsharingapp.dto.rental.RentalSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParametersDto searchParametersDto);
}
