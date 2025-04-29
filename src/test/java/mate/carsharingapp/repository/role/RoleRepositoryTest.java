package mate.carsharingapp.repository.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import mate.carsharingapp.model.Role;
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
@Sql(scripts = "classpath:database/role/add-roles.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @DisplayName("Find role by name")
    @Test
    void findRoleByName() {
        Role.RoleName roleName = Role.RoleName.ROLE_CUSTOMER;
        Role actual = roleRepository.findRoleByName(roleName);

        assertNotNull(actual);
        assertEquals(2, actual.getId());
        assertEquals(roleName, actual.getName());
    }
}
