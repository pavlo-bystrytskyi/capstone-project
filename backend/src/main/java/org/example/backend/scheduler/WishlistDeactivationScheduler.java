package org.example.backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.backend.service.WishlistDeactivationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WishlistDeactivationScheduler {

    private final WishlistDeactivationService wishlistDeactivationService;

    @Scheduled(cron = "${deactivation.schedule}")
    public void scheduleDeactivation() {
        wishlistDeactivationService.deactivateExpired();
    }
}
