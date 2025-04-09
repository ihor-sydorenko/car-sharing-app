package mate.carsharingapp.repository.rental;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.repository.SpecificationProvider;
import mate.carsharingapp.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationProviderManager implements SpecificationProviderManager<Rental> {
    private final List<SpecificationProvider<Rental>> rentalSpecificationProviders;

    @Override
    public SpecificationProvider<Rental> getSpecificationProvider(String key) {
        return rentalSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format(
                        "Can't find correct specification provider for key: %s", key)));
    }
}
