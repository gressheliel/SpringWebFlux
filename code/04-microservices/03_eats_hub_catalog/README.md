# 02 Eats Hub Catalog & Map Struct 

## Dependencias
-  implementation 'org.mapstruct:mapstruct:1.6.3'
-  annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'

## Creación de DTOs
- ReservationRequest, ReservationResponse
- RestaurantResponse

## Evitar choques Lombok & MapStruct
- annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'

## Creación de Mappers
- RestaurantMapper
  
  - RestaurantCollections                                  RestaurantResponse
    private UUID id;
    private String name;                                    private String name;
    private Integer capacity;
    private Address address;                                private Address address;
    private String cuisineType;                             private String cuisineType;
    private PriceEnum priceRange;                           private PriceEnum priceRange;
    private String openHours;                               private String openHours;
    private String logoUrl;                                 private String logoUrl;
    private String closeAt;                                 private String closeAt;
    private ContactInfo contactInfo;                        private ContactInfo contactInfo;
    private List<Review> reviews;                           private Double globalRating;

- ReservationMapper 
  - ReservationCollection                                     ReservationResponse
    private UUID id;
    private String restaurantId;                              private String restaurantId;
    private String customerId;
    private String customerName;                              private String customerName;
    private String customerEmail;
    private String date;                                      private String dateTime; // example 2025-06-16,15:30
    private String time;
    private Integer partySize;                                private Integer partySize;
    private ReservationStatusEnum status;
    private String notes;

## ReservationBusinessService & RestaurantBusinessService
- Se utiliza el operador transform para convertir los DTOs a entidades y viceversa.
- Llamar a los métodos de los mappers reactivos.


## doOnComplete vs doOnSuccess
- doOnComplete se usa en Flux y se ejecuta cuando el flujo emite todos sus elementos y termina correctamente.
- doOnSuccess se usa en Mono y se ejecuta cuando el Mono emite un valor exitosamente (o null).

## Para el creation de un nuevo restaurante
return Mono.just(reservationRequest)                              ReservationRequest => Mono<ReservationRequest>
.transform(reservationMapper::toReservationCollectionMono)        Mono<ReservationRequest> => Mono<ReservationCollection>
.flatMap(reservationCrudService::createReservation)               Mono<ReservationCollection> => Mono<ReservationCollection>
                                                                  (con map)Mono<ReservationCollection> => Mono<Mono<ReservationCollection>>
.map(savedReservation ->                                          Mono<ReservationCollection> => Mono<String>
        String.valueOf(savedReservation.getId()))
.doOnSuccess(id -> 
    log.info("Successfully created reservation with ID: {}", id));



    
    
    
    
    
    
    
    
    