package org.example.backend.service;

import org.example.backend.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GoogleOAuthUserService {

    public User convertToUser(OAuth2User oAuth2User) {
        String id = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");

        return User.builder()
                .externalId(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .picture(picture)
                .build();
    }
}
