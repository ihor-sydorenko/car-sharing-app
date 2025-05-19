package mate.carsharingapp.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import mate.carsharingapp.config.TestUtil;
import mate.carsharingapp.dto.user.UserResponseDto;
import mate.carsharingapp.dto.user.UserUpdateProfileInfoRequestDto;
import mate.carsharingapp.dto.user.UserUpdateRoleRequestDto;
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
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/add-users.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/user/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest {
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

    @DisplayName("Verify updateRole() method - update users roles")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void updateRole_ValidRequest_Success() throws Exception {
        User user = TestUtil.createFirstUser();
        UserUpdateRoleRequestDto requestDto = new UserUpdateRoleRequestDto()
                .setRoleIds(Set.of(2L, 1L));
        UserResponseDto expected = TestUtil.createUserResponseDto(user, requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/{id}/role", user.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @DisplayName("Verify getMyProfile() method - return full information about user")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getMyProfile_AuthenticatedUser_ReturnUserResponseDto() throws Exception {
        User user = TestUtil.createFirstUser();
        UserResponseDto expected = TestUtil.createUserResponseDto(user);

        MvcResult result = mockMvc.perform(get("/users/me")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @DisplayName("Verify updateProfileInfo() method - update profile user info")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void updateProfileInfo_AuthenticatedUser_ReturnUserResponseDto() throws Exception {
        User user = TestUtil.createFirstUser();
        UserUpdateProfileInfoRequestDto requestDto = new UserUpdateProfileInfoRequestDto()
                .setFirstName("newFirstUsername")
                .setLastName("newLastUserName");
        UserResponseDto expected = TestUtil.createUserResponseDto(user);
        expected.setFirstName("newFirstUsername");
        expected.setLastName("newLastUserName");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/users/me")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities())))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Delete user profile by existing id - delete user profile")
    @Test
    void deleteUser_ExistingUserId_DeleteUserProfile() throws Exception {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}
