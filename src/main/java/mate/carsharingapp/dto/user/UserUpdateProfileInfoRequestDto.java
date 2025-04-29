package mate.carsharingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class UserUpdateProfileInfoRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
