package mate.carsharingapp.repository.rental;

import java.util.List;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    List<Rental> findAllByUser(User user, Pageable pageable);

    Rental findByIdAndUser(Long rentalId, User user);

    List<Rental> findAllByUserAndActualReturnDateIsNull(User user);
}
