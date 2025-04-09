package mate.carsharingapp.repository.rental.spec;

import java.util.Arrays;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    public static final String IS_ACTIVE = "isActive";

    @Override
    public String getKey() {
        return IS_ACTIVE;
    }

    @Override
    public Specification<Rental> getSpecification(Object[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(IS_ACTIVE).in(Arrays.stream(params).toArray());
    }
}
