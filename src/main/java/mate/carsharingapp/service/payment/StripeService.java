package mate.carsharingapp.service.payment;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import mate.carsharingapp.model.Rental;
import org.springframework.web.util.UriComponentsBuilder;

public interface StripeService {
    Session createCheckoutSession(Rental rental,
                                  BigDecimal amountToPay,
                                  UriComponentsBuilder uriComponentsBuilder);
}
