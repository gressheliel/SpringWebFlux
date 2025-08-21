# 07 Eats Hub Catalog & Kafka

## Dependencias
dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:2025.0.0"
    }
}

// Kafka
implementation 'org.springframework.cloud:spring-cloud-starter-stream-kafka'


## Componentes
- Copiar la clase RatingEvent de 04-customer-manager, en llamará ReviewEvent
- Convertir el archivo `application.properties` a `application.yml`

## ReviewStreamConfig
- Es un Consumer, ya que solo escucha los mensajes que lleguen a Kafka
- Los Serializa a ReviewEvent y lo regresa al servicio ReviewStreamService

## ReviewStreamService
- Es un servicio que recibe los eventos de ReviewStreamConfig y los procesa

## TEST
- Levantar CustomerManager
- Levantar EatsHubCatalog
- Registrar un usuario : http://localhost:9090/customer/auth/register?roles=free_user,premium_user
- Login de usuario : http://localhost:9090/customer/auth/login Copiar el JWT Token
- Mandar un rating : http://localhost:9090/customer/rating
- Limpiar logs

- Log de CustomerManager
```
16:07:15.328 INFO  [reactor-http-epoll-3] c.e.c.RatingController - Controller Received rating request: RatingRequest(idRestaurant=f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7, uuidCustomer=b1d30e28-bb68-4cfa-86c7-59ad9a1b16db, comment=The food was excellent and the service was very attentive. KAFKA 2, rating=5)
16:07:15.329 INFO  [reactor-http-epoll-3] c.e.s.StreamService - Service Sending rating request: RatingRequest(idRestaurant=f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7, uuidCustomer=b1d30e28-bb68-4cfa-86c7-59ad9a1b16db, comment=The food was excellent and the service was very attentive. KAFKA 2, rating=5)
16:07:15.341 INFO  [reactor-http-epoll-3] c.e.s.RatingStreamsConfig - Sending msg to kafka rating-request-internal: {"idRestaurant":"f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7","uuidCustomer":"b1d30e28-bb68-4cfa-86c7-59ad9a1b16db","comment":"The food was excellent and the service was very attentive. KAFKA 2","rating":5}
16:07:15.354 INFO  [reactor-http-epoll-3] c.e.s.StreamService - Service Successfully emitted message to sink
16:07:15.399 INFO  [KafkaConsumerDestination{consumerDestinationName='rating-request-internal', partitions=1, dlqName='null'}.container-0-C-1] c.e.s.RatingStreamsConfig - Converted rating request to rating event: RatingEvent(idRestaurant=f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7, rating=5, uuidCustomer=b1d30e28-bb68-4cfa-86c7-59ad9a1b16db, comment=The food was excellent and the service was very attentive. KAFKA 2, eventType=RATING_CREATED, timestamp=1755814035397)
```
- Log de EatsHubCatalog
```
16:07:15.462 INFO  [KafkaConsumerDestination{consumerDestinationName='rating-events', partitions=1, dlqName='null'}.container-0-C-1] c.e.s.ReviewStreamConfig - Received review event: {"idRestaurant":"f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7","rating":5,"uuidCustomer":"b1d30e28-bb68-4cfa-86c7-59ad9a1b16db","comment":"The food was excellent and the service was very attentive. KAFKA 2","eventType":"RATING_CREATED","timestamp":1755814035397}
```

## ReviewStreamService
- Recibe los eventos del kafka y los envía a ReviewServiceImpl

## ReviewServiceImpl
- Guarda los reviews del restaurant en la base de datos de MongoDB

# TEST FINALES
- Se realizan como en la seccion de TEST
- Mandar un rating : http://localhost:9090/customer/rating  SE MANDA UN ID DE RESTAURANTE QUE EXISTE
- Validar en se agregó el rating en la base de datos de MongoDB





