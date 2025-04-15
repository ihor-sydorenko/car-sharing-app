package mate.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "rental_id", nullable = false)
    @MapsId
    private Rental rental;
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    @Column(nullable = false, columnDefinition = "VARCHAR(500)")
    private URL sessionUrl;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amountToPay;

    public enum PaymentStatus {
        PENDING, PAID, CANCELLED, EXPIRED
    }

    public enum PaymentType {
        PAYMENT, FINE
    }
}
