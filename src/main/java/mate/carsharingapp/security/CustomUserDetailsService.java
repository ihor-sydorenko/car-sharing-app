package mate.carsharingapp.security;

import lombok.RequiredArgsConstructor;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find user by email: %s", email))
        );
    }
}
