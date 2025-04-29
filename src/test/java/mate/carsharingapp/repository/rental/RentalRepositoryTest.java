package mate.carsharingapp.repository.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/rental/add-rentals-cars-users.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @DisplayName("Find all rentals by existing user")
    @Test
    void findAllByUser_ExistingUser_ReturnListOfRentals() {
        User user = new User();
        user.setId(1L);
        List<Rental> actual = rentalRepository.findAllByUser(user, PageRequest.of(0, 5));
        assertEquals(2, actual.size());
    }

    @DisplayName("Find rental by existing id and user")
    @Test
    void findByIdAndUser_ExistingIdAndUser_ReturnRental() {
        Long rentalId = 1L;
        User user = new User();
        user.setId(1L);
        Optional<Rental> actual = rentalRepository.findByIdAndUser(rentalId, user);
        assertNotNull(actual);
    }

    @DisplayName("Find rental by non exist id")
    @Test
    void findByIdAndUser_IncorrectRentalId_ReturnEmptyList() {
        Long rentalId = 99L;
        User user = new User();
        user.setId(1L);
        Optional<Rental> actual = rentalRepository.findByIdAndUser(rentalId, user);
        assertEquals(Optional.empty(), actual);
    }

    @DisplayName("Find all active rentals by user")
    @Test
    void findAllByUserAndActualReturnDateIsNull_ExistingActiveRental_ReturnListOfRentals() {
        User user = new User();
        user.setId(1L);
        List<Rental> actual = rentalRepository.findAllByUserAndActualReturnDateIsNull(user);
        assertEquals(1, actual.size());
        assertEquals(2, actual.get(0).getId());
    }

    @DisplayName("Find all inactive rentals by user")
    @Test
    void findByIdAndActualReturnDateIsNotNull_ExistingIdInactiveRental_ReturnRental() {
        Long rentalId = 1L;
        Optional<Rental> actual = rentalRepository.findByIdAndActualReturnDateIsNotNull(rentalId);
        assertNotNull(actual);
        assertEquals(1, actual.get().getId());
    }

    @DisplayName("Find all overdue rentals")
    @Test
    void findOverdueRentals_ExistingOverdueRental_ReturnListOfRentals() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Rental> actual = rentalRepository.findOverdueRentals(tomorrow);
        assertNotNull(actual);
        assertEquals(2, actual.get(0).getId());
    }
}
