package mate.carsharingapp.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;
import mate.carsharingapp.model.Payment;

@Data
public class PaymentDto {
    private Long id;
    private Payment.PaymentStatus status;
    private Payment.PaymentType type;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
