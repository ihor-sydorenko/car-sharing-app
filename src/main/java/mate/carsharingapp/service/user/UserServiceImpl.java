package mate.carsharingapp.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.exception.RegistrationException;
import mate.carsharingapp.mapper.UserMapper;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.RoleRepository;
import mate.carsharingapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsUserByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    String.format("User with email: %s already exist. Please try another.",
                            requestDto.getEmail()));
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleRepository.findRoleByName(Role.RoleName.ROLE_CUSTOMER)));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
