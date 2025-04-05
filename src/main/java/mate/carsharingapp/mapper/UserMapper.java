package mate.carsharingapp.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.carsharingapp.config.MapperConfig;
import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.dto.user.UserUpdateProfileInfoRequestDto;
import mate.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    void updateUserRoleFromDto(UserUpdateRoleRequestDto requestDto,
                               @MappingTarget User existingUser);

    void updateUserProfileInfoFromDto(UserUpdateProfileInfoRequestDto requestDto,
                                      @MappingTarget User existingUser);

    @AfterMapping
    default void setRoleIds(@MappingTarget UserResponseDto userResponseDto, User user) {
        userResponseDto.setRoleIds(user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet()));
    }

    @AfterMapping
    default void setRoles(@MappingTarget User user, UserUpdateRoleRequestDto requestDto) {
        Set<Role> roles = requestDto.getRoleIds().stream()
                .map(Role::new)
                .collect(Collectors.toSet());
        user.setRoles(roles);
    }
}
