package mate.carsharingapp.service.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.mapper.PaymentMapper;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.payment.PaymentRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private StripeService stripeService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private NotificationService notificationService;

    private Car car;
    private User user;
    private Rental rental;
    private Authentication authentication;
    private Payment payment;
    private PaymentDto expectedPaymentDto;
    private UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    void setUp() {
        car = createCar();
        user = createUser();
        rental = createRental(user, car);
        authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());
        payment = createPayment(rental);
        expectedPaymentDto = convertPaymentToPaymentDto(payment);
        uriComponentsBuilder = UriComponentsBuilder.newInstance();
    }

    @DisplayName("Verify createPayment() method works")
    @Test
    void createPayment_Valid_ReturnPaymentDto() {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setRentalId(rental.getId());
        requestDto.setPaymentType(Payment.PaymentType.PAYMENT);

        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("1A");
        when(mockSession.getUrl()).thenReturn("http://mock.url");

        when(rentalRepository.findByIdAndActualReturnDateIsNotNull(requestDto.getRentalId()))
                .thenReturn(Optional.of(rental));
        when(stripeService.createCheckoutSession(any(Rental.class), any(BigDecimal.class),
                any(UriComponentsBuilder.class))).thenReturn(mockSession);
        when(paymentMapper.toModel(any(), any(), any())).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(expectedPaymentDto);

        PaymentDto actual = paymentService.createPayment(requestDto, uriComponentsBuilder);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expectedPaymentDto, actual);
        verify(rentalRepository).findByIdAndActualReturnDateIsNotNull(requestDto.getRentalId());
        verify(stripeService).createCheckoutSession(any(Rental.class), any(BigDecimal.class),
                any(UriComponentsBuilder.class));
        verify(paymentMapper).toModel(any(), any(), any());
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(any(Payment.class));
        verifyNoMoreInteractions(rentalRepository, stripeService, paymentMapper,
                paymentRepository);
    }

    @DisplayName("Verify findAll() method works")
    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> page = new PageImpl<>(List.of((payment)));

        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(user);
        when(paymentRepository.findAll(user.getId(), pageable)).thenReturn(page);
        when(paymentMapper.toDto(payment)).thenReturn(expectedPaymentDto);
        Page<PaymentDto> expected = new PageImpl<>(List.of(expectedPaymentDto));

        Page<PaymentDto> actual = paymentService.findAll(user.getId(), pageable, authentication);

        Assertions.assertEquals(expected, actual);
        verify(userDetailsService).loadUserByUsername(authentication.getName());
        verify(paymentRepository).findAll(user.getId(), pageable);
        verify(paymentMapper).toDto(payment);
    }

    @DisplayName("Verify setSuccessPayment() method works. Throw exception")
    @Test
    void setSuccessPayment_InvalidSessionId_ThrowException() {
        String sessionId = "00000";
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        String expected = "Can't find payment by session id: " + sessionId;
        EntityNotFoundException actual = Assertions.assertThrows(EntityNotFoundException.class,
                () -> paymentService.setSuccessPayment(sessionId));

        Assertions.assertEquals(expected, actual.getMessage());
        verify(paymentRepository).findBySessionId(sessionId);
    }

    @DisplayName("Verify setCancelPayment() method works")
    @Test
    void setCancelPayment() throws StripeException {
        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("1A");
        when(mockSession.getUrl()).thenReturn("http://mock.url");

        Payment.PaymentStatus expectedStatus = Payment.PaymentStatus.CANCELLED;
        when(paymentRepository.findBySessionId(mockSession.getId()))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        String actual = paymentService.setCancelPayment(mockSession);

        Assertions.assertEquals(expectedStatus, payment.getStatus());
        Assertions.assertEquals(mockSession.getUrl(), actual);
    }

    @DisplayName("Verify renewPaymentSession() method works")
    @Test
    void renewPaymentSession_ValidRequest_ReturnDto() {
        payment.setStatus(Payment.PaymentStatus.EXPIRED);
        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("1A");
        when(mockSession.getUrl()).thenReturn("http://mock.url");

        when(paymentRepository.findById(any())).thenReturn(Optional.of(payment));
        when(stripeService.createCheckoutSession(any(), any(), any())).thenReturn(mockSession);
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toDto(any())).thenReturn(expectedPaymentDto);
        Payment.PaymentStatus expectedStatus = Payment.PaymentStatus.PENDING;

        PaymentDto actual = paymentService.renewPaymentSession(
                payment.getId(), uriComponentsBuilder);

        Assertions.assertEquals(expectedStatus, payment.getStatus());
        Assertions.assertEquals(expectedPaymentDto, actual);
    }

    private static Car createCar() {
        return new Car()
                .setId(1L)
                .setModel("Jetta GLI")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(149));
    }

    private static Role createRole() {
        return new Role()
                .setId(2L)
                .setName(Role.RoleName.ROLE_CUSTOMER);
    }

    private static User createUser() {
        return new User()
                .setId(2L)
                .setEmail("nelia@example.com")
                .setPassword("user12345")
                .setFirstName("Nelia")
                .setLastName("Sydorenko")
                .setRoles(Set.of(createRole()));
    }

    private static Rental createRental(User user, Car car) {
        return new Rental()
                .setId(1L)
                .setUser(user)
                .setCar(car)
                .setRentalDate(LocalDate.of(2025, 3, 11))
                .setReturnDate(LocalDate.of(2025, 3, 15))
                .setActualReturnDate(LocalDate.of(2025, 3, 15));
    }

    private static Payment createPayment(Rental rental) {
        return new Payment()
                .setId(1L)
                .setRental(rental)
                .setStatus(Payment.PaymentStatus.PENDING)
                .setType(Payment.PaymentType.PAYMENT)
                .setSessionId("1A")
                .setAmountToPay(new BigDecimal("745.00"));
    }

    private static PaymentDto convertPaymentToPaymentDto(Payment payment) {
        return new PaymentDto()
                .setId(1L)
                .setStatus(payment.getStatus())
                .setType(payment.getType())
                .setSessionUrl(payment.getSessionUrl())
                .setSessionId(payment.getSessionId())
                .setAmountToPay(payment.getAmountToPay());
    }
}
