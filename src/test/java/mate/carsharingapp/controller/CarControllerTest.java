package mate.carsharingapp.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.carsharingapp.config.TestUtil;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/car/add-cars.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/car/delete-cars.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CarControllerTest {
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

    @DisplayName("Verify addCar() method - create new car")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void addCar_ValidRequest_CreateNewCar() throws Exception {
        CreateCarRequestDto requestDto = TestUtil.createCarRequestDto();
        CarDetailsInfoDto expected = TestUtil.createCarDetailsInfoDto(3L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CarDetailsInfoDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarDetailsInfoDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @DisplayName("Verify addCar() method with invalid request dto - return Bad request status")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void addCar_InvalidRequest_ReturnBadRequest() throws Exception {
        CreateCarRequestDto requestDto = TestUtil.createCarRequestDto();
        requestDto.setBrand("");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @DisplayName("Verify getAll() method - get all available cars")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getAll_ExistCarsInDb_ReturnAllCars() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CarDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<CarDto>>() {
                });

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(2, actual.size());
    }

    @DisplayName("Verify getCarById() method - return car by id")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getCarById_ExistCarId_ReturnCarDto() throws Exception {
        Long carId = 1L;
        CarDetailsInfoDto expected = TestUtil.createCarDetailsInfoDto(carId);

        MvcResult result = mockMvc.perform(get("/cars/{id}", carId))
                .andExpect(status().isOk())
                .andReturn();

        CarDetailsInfoDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarDetailsInfoDto.class);
        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id", "dailyFee"));
    }

    @DisplayName("Verify updateCarInfo() method - return updated car info")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void updateCarInfo_ValidUpdateRequest_ReturnUpdatedCar() throws Exception {
        Long carId = 1L;
        CreateCarRequestDto requestDto = TestUtil.createCarRequestDto();
        requestDto.setDailyFee(BigDecimal.valueOf(600));
        CarDetailsInfoDto expected = TestUtil.createCarDetailsInfoDto(1L);
        expected.setDailyFee(BigDecimal.valueOf(600));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/cars/{id}", carId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDetailsInfoDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDetailsInfoDto.class);
        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @DisplayName("Verify deleteCarById() method - delete car")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void deleteCarById_ValidCarId_DeleteCar() throws Exception {
        Long carId = 1L;
        mockMvc.perform(delete("/cars/{id}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}
