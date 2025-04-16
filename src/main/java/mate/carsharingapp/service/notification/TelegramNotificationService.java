package mate.carsharingapp.service.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.carsharingapp.exception.NotificationException;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Service
public class TelegramNotificationService extends TelegramLongPollingBot
        implements NotificationService {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    @Override
    public void sendNotification(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificationException("Message not sent");
        }
    }

    @Override
    public String getBotUsername() {
        return "AdminCarSharingNotificationBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = "Sorry, but you can't send messages in this chat";
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificationException("Message not sent");
        }
    }

    @Override
    public void overdueRentalNotification(List<Rental> rentals) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("Upcoming and overdue rental(s) today:")
                .append(System.lineSeparator());
        if (rentals.isEmpty()) {
            sendNotification(Long.parseLong(chatId), notificationBuilder.toString());
        } else {
            rentals.forEach(rental -> {
                notificationBuilder.append("Rental id: ")
                        .append(rental.getId())
                        .append(System.lineSeparator())
                        .append("Car: ")
                        .append(rental.getCar().getBrand())
                        .append(" ")
                        .append(rental.getCar().getModel())
                        .append(System.lineSeparator())
                        .append("Expected return date: ")
                        .append(rental.getReturnDate())
                        .append(System.lineSeparator())
                        .append("User id: ")
                        .append(rental.getUser().getId())
                        .append(System.lineSeparator())
                        .append("Customer: ")
                        .append(rental.getUser().getFirstName())
                        .append(" ")
                        .append(rental.getUser().getLastName())
                        .append(System.lineSeparator())
                        .append("-----------------------------------")
                        .append(System.lineSeparator());
            });
            sendNotification(Long.parseLong(chatId), notificationBuilder.toString());
        }
    }

    @Override
    public void newRentalCreationNotification(Rental rental) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("New rental was created:")
                .append(System.lineSeparator())
                .append("Rental id: ")
                .append(rental.getId())
                .append(System.lineSeparator())
                .append("Return date: ")
                .append(rental.getReturnDate())
                .append(System.lineSeparator())
                .append("Car: ")
                .append(rental.getCar().getBrand())
                .append(" ")
                .append(rental.getCar().getModel())
                .append(System.lineSeparator())
                .append("User id: ")
                .append(rental.getUser().getId())
                .append(System.lineSeparator())
                .append("Customer: ")
                .append(rental.getUser().getFirstName())
                .append(" ")
                .append(rental.getUser().getLastName());
        sendNotification(Long.parseLong(chatId), notificationBuilder.toString());
    }

    public void successfulPaymentNotification(Payment payment) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("Rental was successfully paid:")
                .append(System.lineSeparator())
                .append("Rental id: ")
                .append(payment.getRental().getId())
                .append(System.lineSeparator())
                .append("Amount: ")
                .append(payment.getAmountToPay())
                .append(" USD")
                .append(System.lineSeparator());
        sendNotification(Long.parseLong(chatId), notificationBuilder.toString());
    }
}
