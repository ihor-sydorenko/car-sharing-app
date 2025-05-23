package mate.carsharingapp.repository.rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    @EntityGraph(attributePaths = {"car"})
    Page<Rental> findAll(Specification<Rental> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"car"})
    List<Rental> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"car"})
    Optional<Rental> findByIdAndUser(Long rentalId, User user);

    List<Rental> findAllByUserAndActualReturnDateIsNull(User user);

    Optional<Rental> findByIdAndActualReturnDateIsNotNull(Long rentalId);

    @Query("SELECT r FROM Rental r JOIN FETCH r.user u JOIN FETCH r.car c "
            + "WHERE r.returnDate <= :tomorrow AND r.actualReturnDate IS NULL")
    List<Rental> findOverdueRentals(LocalDate tomorrow);
}
