package mate.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.dto.user.UserUpdateProfileInfoRequestDto;
import mate.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import mate.carsharingapp.model.User;
import mate.carsharingapp.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update user role", description = "Change user roles")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}/role")
    public UserResponseDto updateRole(@RequestBody @Valid UserUpdateRoleRequestDto requestDto,
                                      @PathVariable Long id) {
        return userService.updateRoleById(requestDto, id);
    }

    @Operation(summary = "Get user info", description = "Get info about authenticated user")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/me")
    public UserResponseDto getMyProfile(Authentication authentication) {
        return userService.getUserInfo(authentication);
    }

    @Operation(summary = "Update user profile info",
            description = "Update profile info to authenticated user")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/me")
    public UserResponseDto updateProfileInfo(
            @RequestBody @Valid UserUpdateProfileInfoRequestDto requestDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(requestDto, user.getId());
    }

    @Operation(summary = "Delete user by id", description = "Delete user by id")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
