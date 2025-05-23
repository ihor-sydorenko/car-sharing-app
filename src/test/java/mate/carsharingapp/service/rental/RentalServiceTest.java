package mate.carsharingapp.service.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.carsharingapp.config.TestUtil;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.mapper.CarMapper;
import mate.carsharingapp.mapper.RentalMapper;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.car.CarRepository;
import mate.carsharingapp.repository.rental.RentalRepository;
import mate.carsharingapp.service.notification.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarMapper carMapper;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private NotificationService notificationService;

    private User user;
    private Car car;
    private Rental rental;
    private RentalDto expectedRentalDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        car = TestUtil.createFirstCar();
        user = TestUtil.createSecondUser();
        rental = TestUtil.createUnclosedRental(user, car);
        expectedRentalDto = createRentalDto(car, user, rental);
        authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());
    }

    @DisplayName("Verify createRental() method works")
    @Test
    void createRental_ValidRequestDto_ReturnRentalDto() {
        CreateRentalRequestDto requestDto = TestUtil.createCreateRentalRequestDto();

        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(user);
        when(rentalRepository.findAllByUserAndActualReturnDateIsNull(user))
                .thenReturn(new ArrayList<>());
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        doNothing().when(notificationService).newRentalCreationNotification(any(Rental.class));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expectedRentalDto);

        RentalDto actual = rentalService.createRental(requestDto, authentication);

        assertEquals(expectedRentalDto, actual);
        verify(userDetailsService).loadUserByUsername(authentication.getName());
        verify(rentalRepository).findAllByUserAndActualReturnDateIsNull(user);
        verify(carRepository).findById(car.getId());
        verify(carRepository).save(car);
        verify(rentalRepository).save(any(Rental.class));
        verify(notificationService).newRentalCreationNotification(any(Rental.class));
        verify(rentalMapper).toDto(any(Rental.class));
        verifyNoMoreInteractions(userDetailsService, rentalRepository, carRepository,
                notificationService, rentalMapper);
    }

    @DisplayName("Verify getRentalsByUserId() method works")
    @Test
    void getRentalsByUserId_ValidUserId_ReturnRentalResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(user);
        when(rentalRepository.findAllByUser(user, pageable)).thenReturn(List.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expectedRentalDto);
        List<RentalDto> expected = List.of(expectedRentalDto);

        List<RentalDto> actual = rentalService.getRentalsByUserId(true, pageable, authentication);

        assertEquals(expected, actual);
        verify(userDetailsService).loadUserByUsername(authentication.getName());
        verify(rentalRepository).findAllByUser(user, pageable);
        verify(rentalMapper).toDto(rental);
        verifyNoMoreInteractions(userDetailsService, rentalRepository, rentalMapper);
    }

    @DisplayName("Verify getRentalById() method works")
    @Test
    void getRentalById_ValidRentalId_ReturnRentalDto() {
        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(user);
        when(rentalRepository.findByIdAndUser(rental.getId(), user))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expectedRentalDto);

        RentalDto actual = rentalService.getRentalById(rental.getId(), authentication);

        assertEquals(expectedRentalDto, actual);
        verify(userDetailsService).loadUserByUsername(authentication.getName());
        verify(rentalRepository).findByIdAndUser(rental.getId(), user);
        verify(rentalMapper).toDto(rental);
    }

    @DisplayName("Verify getRentalById() method works. Throw exception")
    @Test
    void getRentalById_InvalidRentalId_ThrowException() {
        Long rentalId = 99L;
        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(user);
        when(rentalRepository.findByIdAndUser(rentalId, user)).thenReturn(Optional.empty());
        String expected = "Can't find rental by id: " + rentalId;

        EntityNotFoundException actual = Assertions.assertThrows(EntityNotFoundException.class,
                () -> rentalService.getRentalById(rentalId, authentication));

        assertEquals(expected, actual.getMessage());
        verify(userDetailsService).loadUserByUsername(authentication.getName());
        verify(rentalRepository).findByIdAndUser(rentalId, user);
        verifyNoMoreInteractions(userDetailsService, rentalRepository);
    }

    @DisplayName("Verify setActualReturnDate() method works")
    @Test
    void setActualReturnDate_ValidRentalId_ReturnRentalDto() {
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalMapper.toDto(rental)).thenReturn(expectedRentalDto);

        RentalDto actual = rentalService.setActualReturnDate(rental.getId());

        assertEquals(expectedRentalDto, actual);
        verify(rentalRepository).findById(rental.getId());
        verify(rentalRepository).save(rental);
        verify(carRepository).findById(car.getId());
        verify(carRepository).save(car);
        verify(rentalMapper).toDto(rental);
        verifyNoMoreInteractions(rentalRepository, carRepository, rentalMapper);
    }

    private RentalDto createRentalDto(Car car, User user, Rental rental) {
        return new RentalDto()
                .setId(rental.getId())
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate())
                .setUserId(user.getId())
                .setCarDetailsInfoDto(carMapper.toDetailsInfoDto(car));
    }
}
