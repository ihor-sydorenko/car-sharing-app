package mate.carsharingapp.config;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import mate.carsharingapp.mapper.CarMapper;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mock;

public class TestUtil {
    @Mock
    private CarMapper carMapper;

    public static UserRegistrationRequestDto createFirstUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail("ihor@example.com")
                .setPassword("qwerty1234")
                .setRepeatPassword("qwerty1234")
                .setFirstName("Ihor")
                .setLastName("Sydorenko");
    }

    public static UserRegistrationRequestDto createSecondUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail("nelia.sydorenko@gmail.com")
                .setPassword("user12345")
                .setRepeatPassword("user12345")
                .setFirstName("Nelia")
                .setLastName("Sydorenko");
    }

    public static User getUserFromUserRegistrationRequestDto(
            UserRegistrationRequestDto requestDto, Role role) {
        return new User()
                .setId(1L)
                .setEmail(requestDto.getEmail())
                .setPassword(requestDto.getPassword())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setRoles(Set.of(role));
    }

    public static Role createManagerRole() {
        return new Role()
                .setId(1L)
                .setName(Role.RoleName.ROLE_MANAGER);
    }

    public static Role createCustomerRole() {
        return new Role()
                .setId(2L)
                .setName(Role.RoleName.ROLE_CUSTOMER);
    }

    public static UserResponseDto createUserResponseDto(User user) {
        Set<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        return new UserResponseDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setRoleIds(roleIds);
    }

    public static UserResponseDto createUserResponseDto(UserRegistrationRequestDto requestDto) {
        return new UserResponseDto()
                .setId(4L)
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setRoleIds(Set.of(2L));
    }

    public static UserResponseDto createUserResponseDto(
            User user, UserUpdateRoleRequestDto requestDto) {
        return new UserResponseDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setRoleIds(requestDto.getRoleIds());
    }

    public static UserUpdateRoleRequestDto createUserUpdateRoleRequestDto() {
        return new UserUpdateRoleRequestDto()
                .setRoleIds(Set.of(createManagerRole().getId(), createCustomerRole().getId()));
    }

    public static User createFirstUser() {
        return new User()
                .setId(1L)
                .setEmail("customer@example.com")
                .setPassword("qwerty12345")
                .setFirstName("Customer")
                .setLastName("Userovski")
                .setRoles(Set.of(createCustomerRole()));
    }

    public static User createSecondUser() {
        return new User()
                .setId(2L)
                .setEmail("nelia@example.com")
                .setPassword("user12345")
                .setFirstName("Nelia")
                .setLastName("Sydorenko")
                .setRoles(Set.of(createCustomerRole()));
    }

    public static User createThirdUser() {
        return new User()
                .setId(3L)
                .setEmail("ihor@example.com")
                .setPassword("user12345")
                .setFirstName("Ihor")
                .setLastName("Sydorenko")
                .setRoles(Set.of(createCustomerRole()));
    }

    @NotNull
    public static CarDto createCarDto() {
        return new CarDto()
                .setModel("Jetta GLI")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN);
    }

    public static Car createFirstCar() {
        return new Car()
                .setId(1L)
                .setModel("Jetta GLI")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(149));
    }

    public static Car createSecondCar() {
        return new Car()
                .setId(2L)
                .setBrand("Jetta2")
                .setModel("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(109))
                .setDeleted(false);
    }

    public static Rental createUnclosedRental(User user, Car car) {
        return new Rental()
                .setId(1L)
                .setUser(user)
                .setCar(car)
                .setRentalDate(LocalDate.of(2025, 3, 11))
                .setReturnDate(LocalDate.of(2025, 3, 15))
                .setActualReturnDate(null);
    }

    public static Rental createClosedRental(User user, Car car) {
        return new Rental()
                .setId(1L)
                .setUser(user)
                .setCar(car)
                .setRentalDate(LocalDate.of(2025, 3, 11))
                .setReturnDate(LocalDate.of(2025, 3, 15))
                .setActualReturnDate(LocalDate.of(2025, 3, 15));
    }

    public static CreateRentalRequestDto createCreateRentalRequestDto() {
        return new CreateRentalRequestDto()
                .setRentalDate(LocalDate.of(2025, 3, 11))
                .setReturnDate(LocalDate.of(2025, 3, 15))
                .setCarId(1L);
    }

    public static RentalDto createRentalDto(CreateRentalRequestDto requestDto) {
        return new RentalDto()
                .setId(4L)
                .setRentalDate(requestDto.getRentalDate())
                .setReturnDate(requestDto.getReturnDate())
                .setCarDetailsInfoDto(createCarDetailsInfoDto())
                .setUserId(createThirdUser().getId());
    }

    public static Payment createPayment(Rental rental) {
        return new Payment()
                .setId(1L)
                .setRental(rental)
                .setStatus(Payment.PaymentStatus.PENDING)
                .setType(Payment.PaymentType.PAYMENT)
                .setSessionId("1A")
                .setAmountToPay(new BigDecimal("745.00"));
    }

    public static PaymentRequestDto createPaymentRequestDto() {
        return new PaymentRequestDto()
                .setRentalId(1L)
                .setPaymentType(Payment.PaymentType.PAYMENT);
    }

    public static PaymentDto convertPaymentToPaymentDto(Payment payment) {
        return new PaymentDto()
                .setId(1L)
                .setStatus(payment.getStatus())
                .setType(payment.getType())
                .setSessionUrl(payment.getSessionUrl())
                .setSessionId(payment.getSessionId())
                .setAmountToPay(payment.getAmountToPay());
    }

    public static PaymentDto createPaymentDto() throws MalformedURLException {
        PaymentRequestDto requestDto = createPaymentRequestDto();
        return new PaymentDto()
                .setId(requestDto.getRentalId())
                .setStatus(Payment.PaymentStatus.PENDING)
                .setType(requestDto.getPaymentType())
                .setSessionUrl(new URL("http://mock.url1"))
                .setSessionId("sessionId2")
                .setAmountToPay(BigDecimal.valueOf(745));
    }

    public static CreateCarRequestDto createCarRequestDto() {
        return new CreateCarRequestDto()
                .setModel("Jetta GLI")
                .setBrand("Volkswagen")
                .setType("SEDAN")
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(149.00));
    }

    public static CarDetailsInfoDto createCarDetailsInfoDto(Long id) {
        CreateCarRequestDto requestDto = createCarRequestDto();
        return new CarDetailsInfoDto()
                .setId(id)
                .setModel(requestDto.getModel())
                .setBrand(requestDto.getBrand())
                .setType(Car.CarType.SEDAN)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(149.00));
    }

    public static CarDetailsInfoDto createCarDetailsInfoDto() {
        return new CarDetailsInfoDto()
                .setId(1L)
                .setModel("Jetta GLI")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(149));
    }

    public static CarDetailsInfoDto createSecondCarDetailsInfoDto() {
        return new CarDetailsInfoDto()
                .setId(2L)
                .setModel("Jetta2")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(109));
    }

    public static RentalDto createRentalResponseDto(Rental rental) {
        return new RentalDto()
                .setId(rental.getId())
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate())
                .setCarDetailsInfoDto(createSecondCarDetailsInfoDto())
                .setUserId(rental.getUser().getId());
    }
}
