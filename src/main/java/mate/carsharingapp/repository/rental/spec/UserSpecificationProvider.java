package mate.carsharingapp.repository.rental.spec;

import jakarta.persistence.criteria.JoinType;
import java.util.Arrays;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Rental> {
    public static final String USER_ID = "user.id";

    @Override
    public String getKey() {
        return USER_ID;
    }

    @Override
    public Specification<Rental> getSpecification(Object[] params) {
        return (root, query, criteriaBuilder) ->
                root.join("user", JoinType.INNER).get("id").in(Arrays.asList(params));
    }
}
