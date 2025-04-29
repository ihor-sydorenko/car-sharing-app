package mate.carsharingapp.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import mate.carsharingapp.dto.user.UserLoginRequestDto;
import mate.carsharingapp.dto.user.UserLoginResponseDto;
import mate.carsharingapp.dto.user.UserRegistrationRequestDto;
import mate.carsharingapp.dto.user.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/add-users.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Verify register() method. Return UserResponseDto")
    @Test
    void register_ValidUserRegistrationRequestDto_RegisterNewUserSuccess() throws Exception {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto();
        UserResponseDto expected = createUserResponseDto(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/auth/register")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @DisplayName("Verify login() method. Return valid JWT token")
    @Test
    void login() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto()
                .setEmail("customer@example.com")
                .setPassword("user12345");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class);
        assertNotNull(actual);
        Field field = Arrays.stream(actual.getClass().getDeclaredFields()).toList().get(0);
        assertEquals(1, actual.getClass().getDeclaredFields().length);
        assertEquals("token", field.getName());
    }

    private static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail("nelia.sydorenko@gmail.com")
                .setPassword("user12345")
                .setRepeatPassword("user12345")
                .setFirstName("Nelia")
                .setLastName("Sydorenko");
    }

    private static UserResponseDto createUserResponseDto(UserRegistrationRequestDto requestDto) {
        return new UserResponseDto()
                .setId(4L)
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setRoleIds(Set.of(2L));
    }
}
