package mate.carsharingapp.mapper;

import java.math.BigDecimal;
import mate.carsharingapp.config.MapperConfig;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);

    @Mapping(target = "type", source = "requestDto.paymentType")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    Payment toModel(PaymentRequestDto requestDto, Rental rental, BigDecimal amountToPay);
}
