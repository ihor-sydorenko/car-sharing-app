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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.rental.CreateRentalRequestDto;
import mate.carsharingapp.dto.rental.RentalDto;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.model.Rental;
import mate.carsharingapp.model.Role;
import mate.carsharingapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        "classpath:database/rental/add-rentals-cars-users.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RentalControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Verify createRental() method - create new rental, return RentalDto")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void createRental_Valid_CreateRental() throws Exception {
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto()
                .setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(4))
                .setCarId(1L);

        User user = createSecondUser();
        RentalDto expected = createRentalDto(requestDto);
        expected.getCarDetailsInfoDto().setInventory(4);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                RentalDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @DisplayName("Verify getRentalBySearchParameters() method - ")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void getRentalBySearchParameters_ValidSearchParams_ReturnRentals() throws Exception {
        User user = createFirstUser();
        user.setRoles(Set.of(createCustomerRole(), createManagerRole()));
        MvcResult result = mockMvc.perform(get("/rentals/search")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .param("is_active", "true")
                        .param("user_ids", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), RentalDto[].class);

        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals(2L, actual[0].getUserId());
    }

    @DisplayName("Verify getRentalByUserId() method")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getRentalByUserId() throws Exception {
        User user = createFirstUser();
        MvcResult result = mockMvc.perform(get("/rentals")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .param("isActive", "true")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<RentalDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<RentalDto>>() {
                });

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
    }

    @DisplayName("Verify getRentalByUserId() method")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getRentalById() throws Exception {
        Long rentalId = 3L;
        User user = createFirstUser();
        RentalDto expected = new RentalDto()
                .setId(rentalId)
                .setRentalDate(LocalDate.of(2025, 4, 25))
                .setReturnDate(LocalDate.of(2025, 4, 27))
                .setCarDetailsInfoDto(createSecondCarDetailsInfoDto())
                .setUserId(user.getId());

        MvcResult result = mockMvc.perform(get("/rentals/{rentalId}", rentalId)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), RentalDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @DisplayName("Verify setActualReturnDate() method")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void setActualReturnDate() throws Exception {
        Long rentalId = 3L;
        User user = createFirstUser();
        user.setRoles(Set.of(createCustomerRole(), createManagerRole()));
        Rental rental = new Rental()
                .setId(rentalId)
                .setRentalDate(LocalDate.of(2025, 4, 25))
                .setReturnDate(LocalDate.of(2025, 4, 27))
                .setCar(createCar())
                .setUser(user);

        RentalDto expected = createRentalResponseDto(rental);
        rental.setActualReturnDate(LocalDate.now());
        expected.getCarDetailsInfoDto().setInventory(11);

        MvcResult result = mockMvc.perform(post("/rentals/{rentalId}/return", rentalId)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                RentalDto.class);

        assertNotNull(actual);
        assertEquals(expected.getCarDetailsInfoDto().getInventory(),
                actual.getCarDetailsInfoDto().getInventory());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    private static User createFirstUser() {
        return new User()
                .setId(2L)
                .setEmail("nelia@example.com")
                .setPassword("user12345")
                .setFirstName("Nelia")
                .setLastName("Sydorenko")
                .setRoles(Set.of(createCustomerRole()));
    }

    private static User createSecondUser() {
        return new User()
                .setId(3L)
                .setEmail("ihor@example.com")
                .setPassword("user12345")
                .setFirstName("Ihor")
                .setLastName("Sydorenko")
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

    private static CarDetailsInfoDto createCarDetailsInfoDto() {
        return new CarDetailsInfoDto()
                .setId(1L)
                .setModel("Jetta GLI")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(149));
    }

    private static CarDetailsInfoDto createSecondCarDetailsInfoDto() {
        return new CarDetailsInfoDto()
                .setId(2L)
                .setModel("Jetta2")
                .setBrand("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(109));
    }

    private static RentalDto createRentalDto(CreateRentalRequestDto requestDto) {
        return new RentalDto()
                .setId(4L)
                .setRentalDate(requestDto.getRentalDate())
                .setReturnDate(requestDto.getReturnDate())
                .setCarDetailsInfoDto(createCarDetailsInfoDto())
                .setUserId(createSecondUser().getId());
    }

    private static Car createCar() {
        return new Car()
                .setId(2L)
                .setBrand("Jetta2")
                .setModel("Volkswagen")
                .setType(Car.CarType.SEDAN)
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(109))
                .setDeleted(false);
    }

    private static RentalDto createRentalResponseDto(Rental rental) {
        return new RentalDto()
                .setId(rental.getId())
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate())
                .setCarDetailsInfoDto(createSecondCarDetailsInfoDto())
                .setUserId(rental.getUser().getId());
    }
}
