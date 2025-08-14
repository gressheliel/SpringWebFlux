## 01-customer-manager
- Configuration for WebClientConfiguration
- RestTemplate esta deperecado
- WebClient, reactivo
- RestClient no reactivo
## DTO's
- ReservationRequest, ReservationResponse
## Clients
- ReservationCrudClient, ReservationCrudClientImpl
  - En el update se usa el retry para que si falla, se vuelva a intentar
## Controller
- ReservationController

## Dependencia del javax.validator
- implementation 'org.springframework.boot:spring-boot-starter-validation'

## Adecuación de la respuesta del Create Reservation
- Inicial  :  .bodyValue(reservation)
- Cambiado :  .bodyValue(Map.of("Resource", "/reservation/" + reservation))
- Se agrega el com.elhg.dtos.ReservationResourceResponse
