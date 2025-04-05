package mate.carsharingapp.service.user;

import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.dto.user.UserUpdateProfileInfoRequestDto;
import mate.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import mate.carsharingapp.exception.RegistrationException;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto updateRoleById(UserUpdateRoleRequestDto requestDto, Long userId);

    UserResponseDto getUserInfo(Authentication authentication);

    UserResponseDto updateProfileInfo(UserUpdateProfileInfoRequestDto requestDto, Long userId);

    void deleteById(Long userId);
}
