package org.example.backend.scheduler;

import org.awaitility.Durations;
import org.example.backend.service.WishlistDeactivationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
        "deactivation.schedule=0/1 * * * * *"
})
class WishlistDeactivationSchedulerTest {

    @MockBean
    private WishlistDeactivationService wishlistDeactivationService;

    @Test
    void scheduleDeactivation() {
        await().atMost(Durations.TWO_SECONDS).untilAsserted(
                () -> verify(wishlistDeactivationService, atLeast(1)).deactivateExpired()
        );
    }
}