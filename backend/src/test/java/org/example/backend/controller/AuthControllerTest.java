package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.UserResponse;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String URL = "/api/auth/me";
    private static final String EXTERNAL_ID_FIRST = "some user external id 1";
    private static final String EXTERNAL_ID_SECOND = "some user external id 2";
    private static final String FIRST_NAME_FIRST = "some first name 1";
    private static final String FIRST_NAME_SECOND = "some first name 2";
    private static final String LAST_NAME_FIRST = "some last name 1";
    private static final String LAST_NAME_SECOND = "some last name 2";
    private static final String EMAIL_FIRST = "first@example.com";
    private static final String EMAIL_SECOND = "second@example.com";
    private static final String PICTURE_FIRST = "https://example.com/picture1.jpg";
    private static final String PICTURE_SECOND = "https://example.com/picture2.jpg";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Get user - successful")
    @DirtiesContext
    void getMe_registeredUser() throws Exception {
        User user = createUserFirst();
        createUserSecond();
        MvcResult mvcResult = mockMvc.perform(
                        get(URL)
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(response, UserResponse.class);

        assertUserResponse(user, userResponse);
    }

    private User createUserFirst() {
        return createUser(
                EXTERNAL_ID_FIRST,
                FIRST_NAME_FIRST,
                LAST_NAME_FIRST,
                EMAIL_FIRST,
                PICTURE_FIRST
        );
    }

    private User createUserSecond() {
        return createUser(
                EXTERNAL_ID_SECOND,
                FIRST_NAME_SECOND,
                LAST_NAME_SECOND,
                EMAIL_SECOND,
                PICTURE_SECOND
        );
    }

    private SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor mockUser() {
        return oidcLogin()
                .idToken(idToken -> idToken.claim("sub", EXTERNAL_ID_FIRST));
    }

    private void assertUserResponse(User user, UserResponse userResponse) {
        assertEquals(user.getFirstName(), userResponse.firstName());
        assertEquals(user.getLastName(), userResponse.lastName());
        assertEquals(user.getEmail(), userResponse.email());
        assertEquals(user.getPicture(), userResponse.picture());
    }

    private User createUser(String externalId,
                            String firstName,
                            String lastName,
                            String email,
                            String picture) {
        User user = User.builder()
                .externalId(externalId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .picture(picture)
                .build();

        return userRepository.save(user);
    }

    @Test
    @DisplayName("Get user - guest")
    @DirtiesContext
    void getMe_guestUser() throws Exception {
        createUserFirst();
        User guest = User.builder()
                .firstName("")
                .lastName("")
                .email("")
                .picture("")
                .build();
        MvcResult mvcResult = mockMvc.perform(
                        get(URL)
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(response, UserResponse.class);

        assertUserResponse(guest, userResponse);
    }
}
