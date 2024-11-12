package org.example.backend.service;

import org.example.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class GoogleOAuthUserServiceTest {

    private static final String EXTERNAL_ID = "some external id";
    private static final String EMAIL = "some email";
    private static final String FIRST_NAME = "some first name";
    private static final String LAST_NAME = "some last name";
    private static final String PICTURE = "some picture";

    OAuth2User oAuth2User = mock(OAuth2User.class);

    @Test
    @DisplayName("Convert OAuth2User to User")
    void convertToUser() {
        when(oAuth2User.getAttribute("sub")).thenReturn(EXTERNAL_ID);
        when(oAuth2User.getAttribute("email")).thenReturn(EMAIL);
        when(oAuth2User.getAttribute("given_name")).thenReturn(FIRST_NAME);
        when(oAuth2User.getAttribute("family_name")).thenReturn(LAST_NAME);
        when(oAuth2User.getAttribute("picture")).thenReturn(PICTURE);

        GoogleOAuthUserService googleOAuthUserService = new GoogleOAuthUserService();
        User user = googleOAuthUserService.convertToUser(oAuth2User);

        assertEquals(EXTERNAL_ID, user.getExternalId());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(PICTURE, user.getPicture());
    }
}