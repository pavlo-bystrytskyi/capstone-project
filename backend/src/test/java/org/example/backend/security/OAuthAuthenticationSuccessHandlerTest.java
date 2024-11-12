package org.example.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.User;
import org.example.backend.service.GoogleOAuthUserService;
import org.example.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor
class OAuthAuthenticationSuccessHandlerTest {

    private static final String APP_URL = "some app url";
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final Authentication authentication = mock(Authentication.class);
    private final OAuth2AuthenticationToken oAuthToken = mock(OAuth2AuthenticationToken.class);
    private final OAuth2User oAuthUser = mock(OAuth2User.class);
    private final UserService userService = mock(UserService.class);
    private final User user = mock(User.class);
    private final GoogleOAuthUserService googleOAuthUserService = mock(GoogleOAuthUserService.class);

    @Test
    @DisplayName("Customer authentication - successful")
    void onAuthenticationSuccess_oAuthToken() throws ServletException, IOException {
        OAuthAuthenticationSuccessHandler handler = new OAuthAuthenticationSuccessHandler(userService, googleOAuthUserService);
        ReflectionTestUtils.setField(handler, "appUrl", APP_URL);
        when(oAuthToken.getPrincipal()).thenReturn(oAuthUser);
        when(googleOAuthUserService.convertToUser(oAuthUser)).thenReturn(user);

        handler.onAuthenticationSuccess(request, response, oAuthToken);

        verify(oAuthToken).getPrincipal();
        verify(googleOAuthUserService).convertToUser(oAuthUser);
        verify(userService).saveUserIfNotExists(user);
        verify(response).sendRedirect(APP_URL);
    }

    @Test
    @DisplayName("Customer authentication - incorrect token type")
    void onAuthenticationSuccess_notOAuthToken() throws ServletException, IOException {
        OAuthAuthenticationSuccessHandler handler = new OAuthAuthenticationSuccessHandler(userService, googleOAuthUserService);
        ReflectionTestUtils.setField(handler, "appUrl", APP_URL);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(oAuthToken, never()).getPrincipal();
        verify(googleOAuthUserService, never()).convertToUser(oAuthUser);
        verify(userService, never()).saveUserIfNotExists(user);
        verify(response).sendRedirect(APP_URL);
    }
}