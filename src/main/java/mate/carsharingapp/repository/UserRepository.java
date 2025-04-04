package mate.carsharingapp.repository;

import java.util.Optional;
import mate.carsharingapp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);
}
