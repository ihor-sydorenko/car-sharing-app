package mate.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.carsharingapp.validation.FieldMatch;
import org.hibernate.validator.constraints.Length;

@Accessors(chain = true)
@Data
@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!"
)
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    @Length(max = 255)
    private String email;
    @NotBlank
    @Length(min = 8, max = 255)
    private String password;
    @NotBlank
    @Length(min = 8, max = 255)
    private String repeatPassword;
    @NotBlank
    @Length(max = 255)
    private String firstName;
    @NotBlank
    @Length(max = 255)
    private String lastName;
}
