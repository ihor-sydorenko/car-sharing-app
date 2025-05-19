package mate.carsharingapp.service.rental;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.dto.rental.RentalSearchParametersDto;
import mate.carsharingapp.exception.CarAvailabilityException;
import mate.carsharingapp.exception.ClosedRentalException;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.mapper.RentalMapper;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.car.CarRepository;
import mate.carsharingapp.repository.rental.RentalRepository;
import mate.carsharingapp.repository.rental.RentalSpecificationBuilder;
import mate.carsharingapp.service.notification.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserDetailsService userDetailsService;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public RentalDto createRental(CreateRentalRequestDto requestDto,
                                  Authentication authentication) {
        User user = (User) userDetailsService.loadUserByUsername(authentication.getName());
        checkUserForUnclosedRentals(user);
        Car car = findCarById(requestDto.getCarId());
        checkCarAvailability(car);
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        Rental rental = createNewRental(requestDto, car, user);
        rentalRepository.save(rental);
        notificationService.newRentalCreationNotification(rental);
        return rentalMapper.toDto(rental);
    }

    @Override
    public List<RentalDto> searchByManager(RentalSearchParametersDto parametersDto,
                                           Pageable pageable) {
        Specification<Rental> rentalSpecification =
                rentalSpecificationBuilder.build(parametersDto);
        return rentalRepository.findAll(rentalSpecification, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public List<RentalDto> getRentalsByUserId(Boolean isActive,
                                              Pageable pageable,
                                              Authentication authentication) {
        User user = (User) userDetailsService.loadUserByUsername(authentication.getName());
        return rentalRepository.findAllByUser(user, pageable).stream()
                .filter(rental -> isActive == null
                        || (isActive && rental.getActualReturnDate() == null)
                        || (!isActive && rental.getActualReturnDate() != null))
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDto getRentalById(Long rentalId, Authentication authentication) {
        User user = (User) userDetailsService.loadUserByUsername(authentication.getName());
        Rental rental = rentalRepository.findByIdAndUser(rentalId, user).orElseThrow(
                () -> new EntityNotFoundException(String.format("Can't find rental by id: %s",
                        rentalId))
        );
        return rentalMapper.toDto(rental);
    }

    @Transactional
    @Override
    public RentalDto setActualReturnDate(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Can't find rental by id: %s",
                        rentalId))
        );
        checkIfRentalIsAlreadyClosed(rental);
        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);
        rentalRepository.save(rental);
        Car car = findCarById(rental.getCar().getId());
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        return rentalMapper.toDto(rental);
    }

    private void checkIfRentalIsAlreadyClosed(Rental rental) {
        if (rental.getActualReturnDate() != null) {
            throw new ClosedRentalException("This rental is already closed");
        }
    }

    private void checkUserForUnclosedRentals(User user) {
        List<Rental> rentalList = rentalRepository.findAllByUserAndActualReturnDateIsNull(user);
        if (!rentalList.isEmpty()) {
            throw new ClosedRentalException("Sorry, but you already have unclosed rental");
        }
    }

    private void checkCarAvailability(Car car) {
        if (car.getInventory() == 0) {
            throw new CarAvailabilityException(
                    String.format("Sorry, but car with id: %s temporarily unavailable for rent. "
                            + "Please choose another", car.getId())
            );
        }
    }

    private Car findCarById(Long carId) {
        return carRepository.findById(carId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find car by id: %s", carId))
        );
    }

    private Rental createNewRental(CreateRentalRequestDto requestDto, Car car, User user) {
        return new Rental()
                .setRentalDate(LocalDate.now())
                .setReturnDate(requestDto.getReturnDate())
                .setActualReturnDate(null)
                .setCar(car)
                .setUser(user)
                .setActive(true);
    }
}
