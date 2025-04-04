package mate.carsharingapp.service.user;

import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
