package mate.carsharingapp.dto.user;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class UserUpdateRoleRequestDto {
    @NotEmpty
    private Set<Long> roleIds = new HashSet<>();
}
