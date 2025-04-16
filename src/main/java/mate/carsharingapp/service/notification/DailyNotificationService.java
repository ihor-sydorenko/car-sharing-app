package mate.carsharingapp.service.notification;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.repository.rental.RentalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DailyNotificationService {
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "30 8 * * *")
    public void sentNotificationAboutOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository
                .findOverdueRentals(LocalDate.now().plusDays(1));
        notificationService.overdueRentalNotification(overdueRentals);
    }
}
