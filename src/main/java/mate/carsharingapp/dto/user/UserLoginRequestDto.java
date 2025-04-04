package mate.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserLoginRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(max = 255)
    private String password;
}
