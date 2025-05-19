package mate.carsharingapp.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.carsharingapp.config.TestUtil;
import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.dto.user.UserUpdateProfileInfoRequestDto;
import mate.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.exception.RegistrationException;
import mate.carsharingapp.mapper.UserMapper;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.role.RoleRepository;
import mate.carsharingapp.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final String PASSWORD = "qwerty1234";
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Role secondRole;
    private User user;
    private UserResponseDto expectedUserResponseDto;

    @BeforeEach
    void setUp() {
        secondRole = TestUtil.createCustomerRole();
        user = TestUtil.createSecondUser();
        expectedUserResponseDto = TestUtil.createUserResponseDto(user);
    }

    @DisplayName("Verify register() method. Register a new user and return UserResponseDto")
    @Test
    void register_ValidUserRegistrationRequestDto_ReturnUserResponseDto()
            throws RegistrationException {
        UserRegistrationRequestDto requestDto = TestUtil.createFirstUserRegistrationRequestDto();
        user = TestUtil.getUserFromUserRegistrationRequestDto(requestDto, secondRole);
        expectedUserResponseDto = TestUtil.createUserResponseDto(user);

        when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD_ENCODER.encode(PASSWORD));
        when(roleRepository.findRoleByName(Role.RoleName.ROLE_CUSTOMER)).thenReturn(secondRole);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedUserResponseDto);

        UserResponseDto actual = userService.register(requestDto);

        assertEquals(expectedUserResponseDto, actual);
        verify(userRepository).existsUserByEmail(user.getEmail());
        verify(userMapper).toModel(requestDto);
        verify(passwordEncoder).encode(PASSWORD);
        verify(roleRepository).findRoleByName(Role.RoleName.ROLE_CUSTOMER);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userMapper, passwordEncoder, roleRepository, userRepository);
    }

    @DisplayName("Verify updateRoleById() method. Update user's roles and return UserResponseDto")
    @Test
    void updateRoleById_ValidUserUpdateRoleRequestDto_ReturnUserResponseDto() {
        UserUpdateRoleRequestDto requestDto = TestUtil.createUserUpdateRoleRequestDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserRoleFromDto(requestDto, user);
        when(userMapper.toDto(user)).thenReturn(expectedUserResponseDto);

        UserResponseDto actual = userService.updateRoleById(requestDto, user.getId());

        assertThat(actual).isEqualTo(expectedUserResponseDto);
        verify(userRepository).findById(anyLong());
        verify(userMapper).updateUserRoleFromDto(requestDto, user);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @DisplayName("Verify updateRoleById() method with non existing id, method throw exception")
    @Test
    void updateRoleById_InvalidUserId_ThrowException() {
        Long userId = 99L;
        UserUpdateRoleRequestDto requestDto = TestUtil.createUserUpdateRoleRequestDto();
        when(userRepository.findById(userId)).thenThrow(
                new EntityNotFoundException("Can't find user by id: " + userId));

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoleById(requestDto, userId));
        String expected = "Can't find user by id: " + userId;

        assertEquals(expected, actual.getMessage());
        assertThat(EntityNotFoundException.class).isEqualTo(actual.getClass());
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("Verify getUserInfo() method, return full information about specific user")
    @Test
    void getUserInfo_ValidAuthentication_ReturnUserResponseDto() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());

        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedUserResponseDto);

        UserResponseDto actual = userService.getUserInfo(authentication);

        assertThat(actual).isEqualTo(expectedUserResponseDto);
        verify(userDetailsService).loadUserByUsername(authentication.getName());
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userDetailsService, userMapper);
    }

    @DisplayName("Verify updateProfileInfo() method, update information about specific user")
    @Test
    void updateProfileInfo_ValidUserUpdateProfileInfoRequestDto_ReturnUserResponseDto() {
        UserUpdateProfileInfoRequestDto requestDto = new UserUpdateProfileInfoRequestDto()
                .setFirstName("Igor")
                .setLastName("Sidorenko");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserProfileInfoFromDto(requestDto, user);
        when(userMapper.toDto(user)).thenReturn(expectedUserResponseDto);

        UserResponseDto actual = userService.updateProfileInfo(requestDto, user.getId());

        assertThat(actual).isEqualTo(expectedUserResponseDto);
        verify(userMapper).updateUserProfileInfoFromDto(requestDto, user);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userDetailsService, userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify deleteById() method with correct id")
    void deleteById_CorrectId_DeleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteById(anyLong());

        verify(userRepository).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
