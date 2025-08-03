package com.elhg.notifications_system;

import com.elhg.notifications_system.models.NotificationEvent;
import com.elhg.notifications_system.models.NotificationStatus;
import com.elhg.notifications_system.models.Priority;
import com.elhg.notifications_system.service.EmailService;
import com.elhg.notifications_system.service.NotificationService;
import com.elhg.notifications_system.service.PhoneService;
import com.elhg.notifications_system.service.TeamsService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class NotificationSystem {
    private static final String NOTIFICATION_CHANNEL_TEAMS = "Teams";
    private static final String NOTIFICATION_CHANNEL_EMAIL = "Email";
    private static final String NOTIFICATION_CHANNEL_PHONE = "Phone";

    //Emite a diferentes canales de notificaciones
    private final Sinks.Many<NotificationEvent> mainEventSink;

    @Getter // Historial Limitado a 50 eventos
    private final Sinks.Many<NotificationEvent> historyEventSink;

    //Servicios de notificaciones
    private final NotificationService teamsService;
    private final NotificationService emailService;
    private final NotificationService phoneService;

    //Emite a un solo canal de notificaciones
    private final Sinks.One<NotificationEvent> teamsEventSink;
    private final Sinks.One<NotificationEvent> emailEventSink;
    private final Sinks.One<NotificationEvent> phoneEventSink;

    //Cache de las notificaciones
    private final ConcurrentMap<String, NotificationEvent> notificationCache;

    public NotificationSystem() {
        //Inicializa los sinks
        this.mainEventSink = Sinks.many().multicast().onBackpressureBuffer();
        this.historyEventSink = Sinks.many().replay().limit(50);

        //Inicializa los servicios de notificaciones
        this.teamsService = new TeamsService();
        this.emailService = new EmailService();
        this.phoneService = new PhoneService();

        //Inicializa los sinks individuales
        this.teamsEventSink = Sinks.one();
        this.emailEventSink = Sinks.one();
        this.phoneEventSink = Sinks.one();

        //Inicializa el cache de notificaciones
        this.notificationCache = new ConcurrentHashMap<>();

        this.setupProcessingFlows();
    }

    // Constructor que permite inyectar los servicios de notificaciones. para los tests
    public NotificationSystem(
            NotificationService teamsService,
            NotificationService emailService,
            NotificationService phoneService) {
        this.mainEventSink = Sinks.many().multicast().onBackpressureBuffer();
        this.historyEventSink = Sinks.many().replay().limit(50);

        this.teamsEventSink = Sinks.one();
        this.emailEventSink = Sinks.one();
        this.phoneEventSink = Sinks.one();

        this.teamsService = teamsService;
        this.emailService = emailService;
        this.phoneService = phoneService;

        this.notificationCache = new ConcurrentHashMap<>();

        setupProcessingFlows();
    }

    private void setupProcessingFlows(){
       mainEventSink
                .asFlux()
                .doOnNext(event -> log.info("Received new event : {} ", event))
                .doOnNext(this::updateNotificationStatus)
                .doOnNext(this.historyEventSink::tryEmitNext)
                .subscribe(this::routeEventByPriority);
        // Alternativa 1
         /*mainEventSink
                .asFlux()
                .subscribe(
                        event -> {
                            log.info("Received new event : {} ", event);
                            updateNotificationStatus(event);
                            historyEventSink.tryEmitNext(event);
                            routeEventByPriority(event);
                        },
                        error -> log.error("Error processing event: {}", error.getMessage()),
                        () -> log.info("All events processed successfully.")
                ); */

        this.setupTeamsProcessor();
        this.setupEmailProcessor();
        this.setupPhoneProcessor();
    }

    /* Método para configurar el procesamiento de eventos de Teams
     *   Este método se encarga de recibir los eventos de Teams y enviarlos al servicio correspondiente.
     *   Utiliza un Mono para procesar los eventos de forma asíncrona y maneja los errores.
     */
    private void setupTeamsProcessor(){
        teamsEventSink
                .asMono()
                .map(event -> teamsService.sendNotification(event)
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnSuccess(success -> updateNotificationSuccess(event, NOTIFICATION_CHANNEL_TEAMS))
                        .doOnError(error -> updateNotificationErrorStatus(event, NOTIFICATION_CHANNEL_TEAMS, error))
                        .onErrorResume(error -> Mono.just(false)))
                .subscribe();
    }

    private void setupEmailProcessor(){
        emailEventSink
                .asMono()
                .flatMap(event -> emailService.sendNotification(event)
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnSuccess(success -> updateNotificationSuccess(event, NOTIFICATION_CHANNEL_EMAIL))
                        .doOnError(error -> updateNotificationErrorStatus(event, NOTIFICATION_CHANNEL_EMAIL, error))
                        .onErrorResume(error -> Mono.just(false)))
                .subscribe();
    }

    private void setupPhoneProcessor(){
        phoneEventSink
                .asMono()
                .flatMap(event -> Mono.defer(()->phoneService.sendNotification(event))
                        .subscribeOn(Schedulers.boundedElastic())
                        .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                        .doOnSuccess(success -> updateNotificationSuccess(event, NOTIFICATION_CHANNEL_PHONE))
                        .doOnError(error -> updateNotificationErrorStatus(event, NOTIFICATION_CHANNEL_PHONE, error))
                        .onErrorResume(error -> Mono.just(false)))
                .subscribe();
    }


    //Metodo para actualizar el status de una notificación
    private void updateNotificationStatus(NotificationEvent notificationEvent) {
        if (Objects.isNull(notificationEvent.getNotificationStatus())) {
            notificationEvent.setId(UUID.randomUUID().toString());
            notificationEvent.setNotificationStatus(NotificationStatus.PENDING);
        }
        //Actualiza el estado de la notificación en el cache
        notificationCache.put(notificationEvent.getId(), notificationEvent);
        log.info("Notification status updated: {}", notificationEvent);
    }

    // Método para actualizar el status de una notificación, en caso de éxito
    private void updateNotificationSuccess(NotificationEvent notificationEvent, String channel) {
        log.info("Notification Success by: {}, event:{}", channel, notificationEvent);
        NotificationEvent event = notificationCache.get(notificationEvent.getId());

        if (Objects.nonNull(event)) {
            event.setNotificationStatus(NotificationStatus.DELIVERED);
            historyEventSink.tryEmitNext(event);
        }
    }

    // Metodo para actualizar el status en caso de falla
    private void updateNotificationErrorStatus(NotificationEvent notificationEvent,
                                               String channel,
                                               Throwable error) {
        log.error("Error to send notification by: {}, for event : {}, error : {}", channel,
                notificationEvent, error.getMessage());

        NotificationEvent event = notificationCache.get(notificationEvent.getId());
        if(Objects.nonNull(event)){
            event.setNotificationStatus(NotificationStatus.FAIlED);
            historyEventSink.tryEmitNext(event);
        }
    }


    /* Método para enrutar el evento de notificación según su prioridad
     *   Este método enruta el evento de notificación a los canales correspondientes
     *   según su prioridad. Si la prioridad es alta, se envía a Teams, Email y Phone.
     *   Si la prioridad es media, se envía a Teams y Email. Si la prioridad es baja,
     *   solo se envía a Teams.
     */
    private void routeEventByPriority(NotificationEvent event){
        teamsEventSink.tryEmitValue(event);

        if(event.getPriority() == Priority.HIGH){
            emailEventSink.tryEmitValue(event);
            phoneEventSink.tryEmitValue(event);
            log.info("Notification event with HIGH priority, sending to : teams, email, phone: {}", event);
        } else if(event.getPriority() == Priority.MEDIUM){
            emailEventSink.tryEmitValue(event);
            log.info("Notification event with MEDIUM priority, sending to : teams, email: {}", event);
        } else {
            log.warn("Notification event with LOW priority, sending to teams: {}", event);
        }
    }

    // Método para publicar un evento de notificación en el sink principal
    public void publishEvent(NotificationEvent notificationEvent) {
        //Emite el evento al sink principal
        mainEventSink.tryEmitNext(notificationEvent);
    }

    public Flux<NotificationEvent> getNotificationHistory() {
        return historyEventSink.asFlux();
    }

    public Mono<NotificationEvent> getNotificationById(String id) {
        return  Mono.justOrEmpty(notificationCache.get(id));
    }

    public Flux<NotificationEvent> retryFailedNotifications() {
        return Flux.fromIterable(notificationCache.values())
                .filter(event -> event.getNotificationStatus() == NotificationStatus.FAIlED)
                .doOnNext(this::publishEvent);
    }
}
