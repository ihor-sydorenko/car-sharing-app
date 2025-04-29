package mate.carsharingapp.repository.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.carsharingapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/add-users.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest {
    private static final String USER_EMAIL = "customer@example.com";
    private static final String USER_FIRST_NAME = "Customer";
    private static final String USER_LAST_NAME = "Userovski";
    @Autowired
    private UserRepository userRepository;

    @DisplayName("Find user by email")
    @Test
    void findUserByEmail_existEmail_ReturnExistUser() {
        Optional<User> actual = userRepository.findUserByEmail(USER_EMAIL);
        assertEquals(USER_FIRST_NAME, actual.get().getFirstName());
        assertEquals(USER_LAST_NAME, actual.get().getLastName());
        assertEquals(1, actual.get().getAuthorities().size());
    }

    @DisplayName("Check if user already registered")
    @Test
    void existsUserByEmail_existEmail_ReturnTrue() {
        boolean actual = userRepository.existsUserByEmail(USER_EMAIL);
        assertTrue(actual);
    }
}
