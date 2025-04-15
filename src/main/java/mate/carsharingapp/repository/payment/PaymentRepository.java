package mate.carsharingapp.repository.payment;

import java.util.List;
import java.util.Optional;
import mate.carsharingapp.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p JOIN p.rental r JOIN r.user u WHERE u.id = :userId")
    Page<Payment> findAll(Long userId, Pageable pageable);

    List<Payment> findAllByStatus(Payment.PaymentStatus status);

    Optional<Payment> findBySessionId(String sessionId);
}
