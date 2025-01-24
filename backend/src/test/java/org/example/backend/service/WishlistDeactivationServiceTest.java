package org.example.backend.service;

import org.example.backend.model.Wishlist;
import org.example.backend.util.TestDataInitializer;
import org.example.backend.util.TestResultVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;
import java.util.List;

@SpringBootTest
@ImportAutoConfiguration
@ComponentScan(basePackages = "org.example.backend.util")
class WishlistDeactivationServiceTest {

    private static final String WISHLIST_TITLE_FIRST = "some wishlist title 1";
    private static final String WISHLIST_TITLE_SECOND = "some wishlist title 2";
    private static final String WISHLIST_DESCRIPTION_FIRST = "some wishlist description 1";
    private static final String WISHLIST_DESCRIPTION_SECOND = "some wishlist description 2";

    @Autowired
    private WishlistDeactivationService wishlistDeactivationService;

    @Autowired
    private TestDataInitializer testDataInitializer;

    @Autowired
    private TestResultVerifier testResultVerifier;

    @Test
    @DirtiesContext
    @DisplayName("Deactivate expired - successful")
    void deactivateExpired_expiredDeactivated() {
        ZonedDateTime dateExpired = ZonedDateTime.now().minusSeconds(1);
        ZonedDateTime dateInFuture = ZonedDateTime.now().plusDays(1);

        Wishlist testedWishlist = testDataInitializer.createWishlist(
                WISHLIST_TITLE_FIRST,
                WISHLIST_DESCRIPTION_FIRST,
                List.of(),
                null,
                true,
                dateExpired
        );

        Wishlist additionalWishlist = testDataInitializer.createWishlist(
                WISHLIST_TITLE_SECOND,
                WISHLIST_DESCRIPTION_SECOND,
                List.of(),
                null,
                true,
                dateInFuture
        );

        wishlistDeactivationService.deactivateExpired();

        testResultVerifier.assertWishlistInTable(null, testedWishlist.withActive(false));
        testResultVerifier.assertWishlistInTable(null, additionalWishlist);
    }

    @Test
    @DirtiesContext
    @DisplayName("Deactivate expired - already inactive")
    void deactivateExpired_alreadyInactive() {
        ZonedDateTime dateExpired = ZonedDateTime.now().minusSeconds(1);
        ZonedDateTime dateInFuture = ZonedDateTime.now().plusDays(1);

        Wishlist testedWishlist = testDataInitializer.createWishlist(
                WISHLIST_TITLE_FIRST,
                WISHLIST_DESCRIPTION_FIRST,
                List.of(),
                null,
                false,
                dateExpired
        );

        Wishlist additionalWishlist = testDataInitializer.createWishlist(
                WISHLIST_TITLE_SECOND,
                WISHLIST_DESCRIPTION_SECOND,
                List.of(),
                null,
                true,
                dateInFuture
        );

        wishlistDeactivationService.deactivateExpired();

        testResultVerifier.assertWishlistInTable(null, testedWishlist);
        testResultVerifier.assertWishlistInTable(null, additionalWishlist);
    }
}
