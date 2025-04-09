package mate.carsharingapp.repository.rental;

import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.rental.RentalSearchParametersDto;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.repository.SpecificationBuilder;
import mate.carsharingapp.repository.SpecificationProviderManager;
import mate.carsharingapp.repository.rental.spec.IsActiveSpecificationProvider;
import mate.carsharingapp.repository.rental.spec.UserSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchParametersDto searchParameters) {
        Specification<Rental> specification = Specification.where(null);
        if (searchParameters.user_ids() != null && searchParameters.is_active().length > 0) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(IsActiveSpecificationProvider.IS_ACTIVE)
                    .getSpecification(searchParameters.is_active()));
        }
        if (searchParameters.user_ids() != null && searchParameters.user_ids().length > 0) {
            specification = specification.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(UserSpecificationProvider.USER_ID)
                    .getSpecification(searchParameters.user_ids()));
        }
        return specification;
    }
}
