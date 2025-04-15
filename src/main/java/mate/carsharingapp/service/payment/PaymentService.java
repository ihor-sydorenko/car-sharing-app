package mate.carsharingapp.service.payment;

import com.stripe.exception.StripeException;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

public interface PaymentService {
    PaymentDto createPayment(PaymentRequestDto requestDto,
                             UriComponentsBuilder uriComponentsBuilder);

    Page<PaymentDto> findAll(Long userId,
                             Pageable pageable,
                             Authentication authentication);

    void setSuccessPayment(String sessionId);

    String setCancelPayment(String sessionId) throws StripeException;

    PaymentDto renewPaymentSession(Long paymentId,
                                   UriComponentsBuilder uriComponentsBuilder);
}
