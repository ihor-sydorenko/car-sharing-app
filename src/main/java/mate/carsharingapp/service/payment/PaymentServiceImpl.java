package mate.carsharingapp.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.exception.UserAccessException;
import mate.carsharingapp.mapper.PaymentMapper;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.payment.PaymentRepository;
import mate.carsharingapp.repository.rental.RentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.5");
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final UserDetailsService userDetailsService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentRequestDto requestDto,
                                    UriComponentsBuilder uriComponentsBuilder) {
        Rental rental = rentalRepository.findByIdAndActualReturnDateIsNotNull(
                requestDto.getRentalId()).orElseThrow(() -> new EntityNotFoundException(
                String.format("Rental with id: %s don't exist or not closed",
                        requestDto.getRentalId()))
        );
        BigDecimal amountToPay = calculateAmountToPay(rental);

        Session session = stripeService.createCheckoutSession(
                rental, amountToPay, uriComponentsBuilder);

        Payment payment = paymentMapper.toModel(requestDto, rental, amountToPay);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setSessionId(session.getId());
        try {
            payment.setSessionUrl(new URL(session.getUrl()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + session.getUrl(), e);
        }
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentDto> findAll(Long userId,
                                    Pageable pageable,
                                    Authentication authentication) {
        checkAccessToPayments(userId, authentication);
        return paymentRepository.findAll(userId, pageable).map(paymentMapper::toDto);
    }

    @Override
    @Transactional
    public void setSuccessPayment(String sessionId) {
        Payment payment = findPayment(sessionId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public String setCancelPayment(String sessionId) throws StripeException {
        Payment payment = findPayment(sessionId);
        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
        Session session = Session.retrieve(sessionId);
        return session.getUrl();
    }

    @Override
    @Transactional
    public PaymentDto renewPaymentSession(Long paymentId,
                                          UriComponentsBuilder uriComponentsBuilder) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find a payment by id: %s", paymentId))
        );

        if (payment.getStatus() != Payment.PaymentStatus.EXPIRED) {
            throw new IllegalStateException("Sorry, but you can renewed only expired payments");
        }
        Session newSession = stripeService.createCheckoutSession(payment.getRental(),
                payment.getAmountToPay(), uriComponentsBuilder);

        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setSessionId(newSession.getId());
        try {
            payment.setSessionUrl(new URL(newSession.getUrl()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + newSession.getUrl(), e);
        }
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    private Payment findPayment(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find payment by session id: %s", sessionId))
        );
    }

    private BigDecimal calculateAmountToPay(Rental rental) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        if (rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            long rentalDuration =
                    ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
            long overdueRentalDuration =
                    ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
            BigDecimal rentalFine = dailyFee.multiply(FINE_MULTIPLIER)
                    .multiply(BigDecimal.valueOf(overdueRentalDuration));
            return dailyFee.multiply(BigDecimal.valueOf(rentalDuration).add(rentalFine));
        }
        long rentalDuration = Math.max(1, ChronoUnit.DAYS.between(
                rental.getRentalDate(), rental.getActualReturnDate()));
        return dailyFee.multiply(BigDecimal.valueOf(rentalDuration));
    }

    private void checkAccessToPayments(Long userId, Authentication authentication) {
        User user = (User) userDetailsService.loadUserByUsername(authentication.getName());
        boolean isCustomer = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(Role.RoleName.ROLE_CUSTOMER));
        if (isCustomer && !user.getId().equals(userId)) {
            throw new UserAccessException("Sorry, but you can get only your payments");
        }
    }
}
