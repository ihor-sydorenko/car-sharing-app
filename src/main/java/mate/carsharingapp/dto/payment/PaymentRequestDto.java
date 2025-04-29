package mate.carsharingapp.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.carsharingapp.model.Payment;

@Accessors(chain = true)
@Data
public class PaymentRequestDto {
    @Positive
    private Long rentalId;
    @NotNull
    private Payment.PaymentType paymentType;
}
