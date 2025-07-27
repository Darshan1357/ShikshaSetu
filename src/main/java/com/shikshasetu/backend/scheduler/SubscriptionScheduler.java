package com.shikshasetu.backend.scheduler;

import com.shikshasetu.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void expireDaily() {
        subscriptionService.expireOldSubscriptions();
    }
}
