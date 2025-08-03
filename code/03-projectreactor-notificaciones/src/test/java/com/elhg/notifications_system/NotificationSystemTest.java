package com.elhg.notifications_system;

import com.elhg.notifications_system.models.NotificationEvent;
import com.elhg.notifications_system.models.NotificationStatus;
import com.elhg.notifications_system.models.Priority;
import com.elhg.notifications_system.service.EmailService;
import com.elhg.notifications_system.service.NotificationService;
import com.elhg.notifications_system.service.PhoneService;
import com.elhg.notifications_system.service.TeamsService;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NotificationSystemTest {

    private TeamsService mockTeamService;
    private EmailService mockEmailService;
    private PhoneService mockPhoneService;

    private NotificationSystem notificationSystem;

    private AtomicInteger teamsCallCount;
    private AtomicInteger emailCallCount;
    private AtomicInteger phoneCallCount;

    @BeforeEach
    void setup() {
        teamsCallCount = new AtomicInteger(0);
        emailCallCount = new AtomicInteger(0);
        phoneCallCount = new AtomicInteger(0);

        mockEmailService = mock(EmailService.class);
        mockPhoneService = mock(PhoneService.class);
        mockTeamService = mock(TeamsService.class);

        when(mockTeamService.sendNotification(any()))
                .thenAnswer(invocation -> {
                    teamsCallCount.incrementAndGet();
                    return Mono.just(true);
                });

        when(mockEmailService.sendNotification(any()))
                .thenAnswer(invocation -> {
                    emailCallCount.incrementAndGet();
                    return Mono.just(true);
                });

        when(mockPhoneService.sendNotification(any()))
                .thenAnswer(invocation -> {
                    phoneCallCount.incrementAndGet();
                    return Mono.just(true);
                });

        this.notificationSystem = new NotificationSystem(mockTeamService,
                mockEmailService, mockPhoneService);
    }


    @Test
    @DisplayName("2. Show send events with LOW priority")
    //@Order(2)
    void testLowPriorityNotification() {
        NotificationEvent event = createTestEvent(Priority.LOW);
        notificationSystem.publishEvent(event);

        // Verify that only the teams service was called
        verify(mockTeamService, times(1)).sendNotification(any());
        verify(mockEmailService, never()).sendNotification(any());
        verify(mockPhoneService, never()).sendNotification(any());

        assert teamsCallCount.get() == 1;
        assert emailCallCount.get() == 0;
        assert phoneCallCount.get() == 0;
    }


    @Test
    @DisplayName("3. Should send events with MEDIUM priority")
    //@Order(3)
    void mediumPriorityEventsShouldGoToTeamsAndEmail() {
        NotificationEvent testEvent = createTestEvent(Priority.MEDIUM);

        notificationSystem.publishEvent(testEvent);

        verify(mockTeamService, times(1)).sendNotification(any());
        verify(mockEmailService, times(1)).sendNotification(any());
        verify(mockPhoneService, never()).sendNotification(any());

        assert teamsCallCount.get() == 1;
        assert emailCallCount.get() == 1;
        assert phoneCallCount.get() == 0;
    }

    @Test
    @DisplayName("4. Should send events with HIGH priority")
    //@Order(4)
    void highPriorityEventsShouldGoToAllChannels() {
        NotificationEvent testEvent = createTestEvent(Priority.HIGH);

        notificationSystem.publishEvent(testEvent);

        verify(mockTeamService, times(1)).sendNotification(any());
        verify(mockEmailService, times(1)).sendNotification(any());
        verify(mockPhoneService, times(1)).sendNotification(any());

        assert teamsCallCount.get() == 1;
        assert emailCallCount.get() == 1;
        assert phoneCallCount.get() == 1;
    }


    @Test
    @DisplayName("5. Show history keep last 3 events")
    //@Order(5)
    void shouldHistoryKeep3Events(){
        NotificationEvent testEvent1 = createTestEvent(Priority.LOW);
        NotificationEvent testEvent2 = createTestEvent(Priority.MEDIUM);
        NotificationEvent testEvent3 = createTestEvent(Priority.HIGH);

        notificationSystem.publishEvent(testEvent1);
        notificationSystem.publishEvent(testEvent2);
        notificationSystem.publishEvent(testEvent3);

        StepVerifier.create(notificationSystem.getNotificationHistory().take(3))
                .expectNextCount(3)
                .verifyComplete();

    }


    @Test
    @DisplayName("6. Show retry 3 attempts when phone service fails")
    //@Order(6)
    void testRetryPhoneAttempts(){
        AtomicInteger attempts = new AtomicInteger(0);

        when(mockPhoneService.sendNotification(any()))
                .thenAnswer(invocation -> {
                    int currentAttempts = attempts.incrementAndGet();
                    if (currentAttempts <= 2) {
                        return Mono.error(new RuntimeException("Error on send notification to Phone"));
                    }else{
                        phoneCallCount.incrementAndGet();
                        System.out.println("phoneCallCount : "+ phoneCallCount.get());
                        return Mono.just(true);                    }
                });
        NotificationEvent testEvent = createTestEvent(Priority.HIGH);
        notificationSystem.publishEvent(testEvent);

        sleep(2000);
        assert attempts.get() >= 3;
        assert phoneCallCount.get() == 2;
    }


    @Test
    @DisplayName("1. How to use virtual time")
    //@Order(1)
    void testVirtualTime() {
        VirtualTimeScheduler scheduler = VirtualTimeScheduler.create();

        NotificationService teams = mock(TeamsService.class);
        NotificationService email = mock(EmailService.class);
        NotificationService phone = mock(PhoneService.class);

        when(teams.sendNotification(any()))
                .thenAnswer(inv -> Mono.just(true).delayElement(Duration.ofMillis(150),scheduler));

        when(email.sendNotification(any()))
                .thenAnswer(inv -> Mono.just(true).delayElement(Duration.ofMillis(300),scheduler));

        when(phone.sendNotification(any()))
                .thenAnswer(inv -> Mono.just(true).delayElement(Duration.ofMillis(1000),scheduler));

        NotificationSystem testSystem = new NotificationSystem(teams, email, phone);
        NotificationEvent testEvent = createTestEvent(Priority.HIGH);

        testSystem.publishEvent(testEvent);
        scheduler.advanceTimeBy(Duration.ofMillis(1500));

        StepVerifier.withVirtualTime(()-> testSystem.getNotificationHistory().take(1))
                .expectNextMatches(element -> element.getNotificationStatus() == NotificationStatus.DELIVERED)
                .verifyComplete();

    }

    private NotificationEvent createTestEvent(Priority priority) {
        return NotificationEvent.builder()
                .id(UUID.randomUUID().toString())
                .source("TEST")
                .message("Test msg with priority: " + priority.toString())
                .priority(priority)
                .timestamp(LocalDateTime.now())
                .notificationStatus(NotificationStatus.PENDING)
                .build();
    }

    private void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
