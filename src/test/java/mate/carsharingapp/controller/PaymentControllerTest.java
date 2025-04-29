package mate.carsharingapp.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import mate.carsharingapp.config.CustomPageImpl;
import mate.carsharingapp.dto.payment.PaymentDto;
import mate.carsharingapp.dto.payment.PaymentRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.model.Payment;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import mate.carsharingapp.repository.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/user/delete-all.sql",
        "classpath:database/rental/add-rentals-cars-users.sql",
        "classpath:database/payment/add-payments.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaymentControllerTest {

    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Verify createPayment() method - ")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void createPayment() throws Exception {
        User user = createUser();
        PaymentRequestDto requestDto = createPaymentRequestDto();
        PaymentDto expected = createPaymentDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/payments")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentDto.class);

        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "sessionUrl", "sessionId"));
    }

    @DisplayName("Verify getAllPayments() method - ")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getAllPayments() throws Exception {
        User user = createUser();
        MvcResult result = mockMvc.perform(get("/payments")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .param("user_id", "1")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(CustomPageImpl.class, RentalDto.class);
        PageImpl<RentalDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.getContent().size());
    }

    @DisplayName("Verify successPayment() method - ")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void successPayment() throws Exception {
        mockMvc.perform(get("/payments/success")
                        .param("sessionId", "sessionId1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<Payment> payment = paymentRepository.findBySessionId("sessionId1");

        assertTrue(payment.isPresent());
        assertEquals(Payment.PaymentStatus.PAID, payment.get().getStatus());
    }

    private PaymentRequestDto createPaymentRequestDto() {
        return new PaymentRequestDto()
                .setRentalId(1L)
                .setPaymentType(Payment.PaymentType.PAYMENT);
    }

    private PaymentDto createPaymentDto() throws MalformedURLException {
        PaymentRequestDto requestDto = createPaymentRequestDto();
        return new PaymentDto()
                .setId(requestDto.getRentalId())
                .setStatus(Payment.PaymentStatus.PENDING)
                .setType(requestDto.getPaymentType())
                .setSessionUrl(new URL("http://mock.url1"))
                .setSessionId("sessionId2")
                .setAmountToPay(BigDecimal.valueOf(745));
    }

    private static User createUser() {
        return new User()
                .setId(1L)
                .setEmail("customer@example.com")
                .setPassword("user12345")
                .setFirstName("Customer")
                .setLastName("Userovski")
                .setRoles(Set.of(createCustomerRole()));
    }

    private static Role createManagerRole() {
        return new Role()
                .setId(1L)
                .setName(Role.RoleName.ROLE_MANAGER);
    }

    private static Role createCustomerRole() {
        return new Role()
                .setId(2L)
                .setName(Role.RoleName.ROLE_CUSTOMER);
    }
}
