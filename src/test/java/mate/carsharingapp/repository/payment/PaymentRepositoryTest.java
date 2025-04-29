package mate.carsharingapp.repository.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import mate.carsharingapp.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/rental/add-rentals-cars-users.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/payment/add-payments.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("Find all payments by existing user id")
    @Test
    void findAll_ExistingUserId_ReturnPayment() {
        Long userId = 1L;
        Page<Payment> actual = paymentRepository.findAll(userId, PageRequest.of(0, 5));
        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
    }

    @DisplayName("Find all payments by correct payment status")
    @Test
    void findAllByStatus_CorrectStatus_ReturnListOfPayments() {
        Payment.PaymentStatus status = Payment.PaymentStatus.PENDING;
        List<Payment> actual = paymentRepository.findAllByStatus(status);
        assertNotNull(actual);
        assertEquals(1, actual.size());
    }

    @DisplayName("Find payment by session id")
    @Test
    void findBySessionId_ExistingSessionId_ReturnPayment() {
        String sessionId = "sessionId1";
        Optional<Payment> actual = paymentRepository.findBySessionId(sessionId);
        assertNotNull(actual);
        assertEquals(2, actual.get().getId());
    }
}
