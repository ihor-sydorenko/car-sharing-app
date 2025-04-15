package mate.carsharingapp.repository.rental;

import java.util.List;
import java.util.Optional;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    List<Rental> findAllByUser(User user, Pageable pageable);

    Optional<Rental> findByIdAndUser(Long rentalId, User user);

    List<Rental> findAllByUserAndActualReturnDateIsNull(User user);

    Optional<Rental> findByIdAndActualReturnDateIsNotNull(Long rentalId);
}
