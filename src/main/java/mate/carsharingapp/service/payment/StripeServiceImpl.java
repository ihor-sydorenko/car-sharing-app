package mate.carsharingapp.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.exception.CreateSessionException;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.repository.payment.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {
    private static final String CURRENCY = "usd";
    private final PaymentRepository paymentRepository;
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Override
    public Session createCheckoutSession(Rental rental,
                                         BigDecimal amountToPay,
                                         UriComponentsBuilder uriComponentsBuilder) {
        Stripe.apiKey = stripeSecretKey;
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(uriComponentsBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8080)
                        .path("/payments/success")
                        .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                        .build()
                        .toString())
                .setCancelUrl(uriComponentsBuilder.path("/payments/cancel").build().toString())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(CURRENCY)
                                .setUnitAmount(amountToPay.longValue() * 100L)
                                .setProductData(SessionCreateParams
                                        .LineItem.PriceData.ProductData.builder()
                                        .setName(String.format("Payment for renting car: %s, %s",
                                                rental.getCar().getBrand(),
                                                rental.getCar().getModel()))
                                        .build())
                                .build())
                        .build())
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new CreateSessionException("Can't create session");
        }
        return session;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void checkExpiredSessions() {
        List<Payment> pendingPayments = paymentRepository
                .findAllByStatus(Payment.PaymentStatus.PENDING);

        for (Payment payment : pendingPayments) {
            try {
                Session session = Session.retrieve(payment.getSessionId());
                if (session.getExpiresAt() < System.currentTimeMillis() / 1000) {
                    payment.setStatus(Payment.PaymentStatus.EXPIRED);
                    paymentRepository.save(payment);
                }
            } catch (StripeException e) {
                throw new CreateSessionException(
                        String.format("Failed retrieve session for payment id: %s",
                                payment.getId()));
            }
        }
    }
}
