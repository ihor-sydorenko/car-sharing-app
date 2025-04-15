package mate.carsharingapp.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import mate.carsharingapp.service.payment.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payments management", description = "Endpoints for management payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Create new payment", description = "Create Stripe session fro payment")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentDto createPayment(@RequestBody PaymentRequestDto requestDto,
                                    UriComponentsBuilder uriComponentsBuilder) {
        return paymentService.createPayment(requestDto, uriComponentsBuilder);
    }

    @Operation(summary = "Get all payments", description = "Get all user payments")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping
    public Page<PaymentDto> getAllPayments(@RequestParam(value = "user_id", required = false)
                                           Long userId,
                                           Pageable pageable,
                                           Authentication authentication) {
        return paymentService.findAll(userId, pageable, authentication);
    }

    @Operation(summary = "Change payment status to successful",
            description = "Endpoint for stripe redirection")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/success")
    public String successPayment(@RequestParam String sessionId) {
        paymentService.setSuccessPayment(sessionId);
        return "Payment successful page";
    }

    @Operation(summary = "Change payment status to cancelled",
            description = "Endpoint for stripe redirection. Return payment canceled message")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestParam String sessionId)
            throws StripeException {
        String url = paymentService.setCancelPayment(sessionId);
        String message = String.format("Payment has been canceled."
                + " Follow this link to finish payment in next 24 hours: %s", url
        );
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Renew payment", description = "Create new session for payment")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/{paymentId}")
    public PaymentDto renewPayment(@PathVariable Long paymentId,
                                   UriComponentsBuilder uriComponentsBuilder) {
        return paymentService.renewPaymentSession(paymentId, uriComponentsBuilder);
    }
}
