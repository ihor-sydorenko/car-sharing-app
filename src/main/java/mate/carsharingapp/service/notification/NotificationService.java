package mate.carsharingapp.service.notification;

import java.util.List;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;

public interface NotificationService {

    void sendNotification(Long chatId, String message);

    void overdueRentalNotification(List<Rental> rentals);

    void newRentalCreationNotification(Rental rental);

    void successfulPaymentNotification(Payment payment);
}
