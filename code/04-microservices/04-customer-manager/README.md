## 04 Kafka, Reactive Cloud Streams
- **Broker**: Servidor que almacena y gestiona mensajes. Un clúster Kafka tiene varios brokers para distribuir la carga y tolerar fallos.
- **Topic**: Canal o categoría donde se publican los mensajes. Los productores envían mensajes a un topic y los consumidores los leen de ahí.
- **Partition**: Cada topic se divide en particiones, permitiendo distribuir los datos y el procesamiento entre varios brokers y consumidores. Mejora el rendimiento y la escalabilidad.
- **Offset**: Identificador único y secuencial de cada mensaje dentro de una partición. Permite a los consumidores saber qué mensajes ya han leído.
- **Producer**: Componente que envía mensajes a un topic.
- **Consumer**: Componente que lee mensajes de un topic.
- **Replication**: Copia de particiones en varios brokers para tolerancia a fallos.
- **ZooKeeper**: Sistema que gestiona la coordinación y el estado del clúster Kafka (en versiones antiguas).



## Agregar kafka y kafka-ui
- Se configura el docker-compose para levantar kafka y kafka-ui
- kafka-ui, se accede a través de http://localhost:8090

## Creación de topic y productor consumidor
-- Crear un topic llamado `rating-events`
docker exec -it kafka-broker \
kafka-topics.sh --bootstrap-server localhost:9092 \
--create --topic rating-events --partitions 1 --replication-factor 1

-- Producir mensajes al topic `rating-events`
docker exec -it kafka-broker \
kafka-console-producer.sh --bootstrap-server localhost:9092 \
--topic rating-events

-- Producir mensajes al topic `rating-request-internal`
docker exec -it kafka-broker \
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic rating-request-internal --from-beginning --property print.key=true

-- Consumir mensajes del topic `rating-events`
docker exec -it kafka-broker \
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic rating-events --from-beginning

## Dependencias
- implementation 'org.springframework.cloud:spring-cloud-starter-stream-kafka'
- Se agrega spring cloud
```
dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:2025.0.0"
    }
}
```

## Componentes
- RetryableStreamException => Reintentos de kafka
- StreamTerminateException => Excepción fatal, indica que el flujo terminó
- RatingEvent, RatingRequest => Estructura de lo que se envía a kafka

## StreamService 
- Sink, emitir eventos de manera asincrónica mediante un Flux
  El código seleccionado convierte un objeto RatingRequest a JSON y lo envía como mensaje a un Sink reactivo. 
- Si la emisión falla, se mapea el error y se intenta reintentar hasta 3 veces usando backoff exponencial, 
- pero solo si el error es de tipo RetryableStreamException. Se registra el éxito o el error en el log.

Fragmentos clave:
Serialización a JSON:  ```Mono.fromCallable(() -> objectMapper.writeValueAsString(request))```
Construcción y emisión del mensaje:
```
Message<String> message = MessageBuilder.withPayload(json)
.setHeader(KafkaHeaders.KEY, request.getIdRestaurant().toString().getBytes())
.setHeader("contentType", "text/plain")
.build();
Sinks.EmitResult result = ratingRequestSink.tryEmitNext(message);
```
Manejo de errores y reintentos:
```
.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
.filter(throwable -> throwable instanceof RetryableStreamException)
.onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
.doOnError(throwable -> log.error("Error sending rating request to stream: {}", throwable.getMessage()));
```
En resumen, el código gestiona el envío de mensajes de forma reactiva, con manejo de errores y reintentos controlados.

## RatingStreamConfig
- Controller llama al ServiceStream, Sink emite el mensaje y lo inyecta al Supplier que lo envía  a traves de kafka
- Se configura el Sink y Supplier para enviar mensajes a Kafka
- El mensaje se transforma en RatingEvent(Se hace con una interfaz function)

- La función seleccionada (transformRatingRequestToRatingEvent) es un bean de Spring que transforma un flujo de mensajes 
- de solicitudes de calificación (RatingRequest) en mensajes de eventos de calificación (RatingEvent). 
- Utiliza programación reactiva con Flux y el método handle para procesar cada mensaje

- Agregar función de cloud de kafka en el properties
```
spring:
  cloud:
    function:
      definition: rattingRequestSupplier;rattingTransformer
```

## Binders
- Infraestructura de kafka
- Kafka es el binder predeterminado para Spring Cloud Stream.
- RabbitMQ es otro binder comúnmente utilizado.
- Spring Cloud Stream utiliza binders para conectar aplicaciones a sistemas de mensajería como Kafka.

```
spring:
  cloud:
    stream:
      kafka:
        binder:
          brokers: localhost:9092
          auto-create-topics: 'true'
          producer-properties:
            ack: all
            enable.idempotence: 'true'

```

## Binding
- Mappeo de las funciones que definimos
- 1. JSON de entrada a traves del controller
- 2. Se recibe en el Supplier, lo recibe a traves del Sink

-  El flujo comienza en el Supplier
-  Supplier -> JSON -> Function -> new JSON -> Consumer 
- Supplier provee el JSON (NO TIENE CAPACIDAD DE CONSUMIR)
- Function recibe el JSON (A traves del canal de Kafka) lo transforma y lo envía (A traves del canal de Kafka)
- Consumer recibe el JSON (NO TIENE CAPACIDAD DE PRODUCIR)
```

```

## RattingController
- Controller que recibe el JSON y lo envía al Sink

## Flujo explicación
- RatingController                Recibe request(RatingRequest)
- StreamService                   Recibe request(RatingRequest) y lo emite al Sink

- En las configuraciones del properties:
  - Mediante los binders se enlazan los tópicos(kafka) a las Function/Suppliers
  - Mediante los binding se configuran el comportamiento del tópico (Si será de in ó in/out)
    - rating-request-internal -> Supplier -> out-0 -> Este tópico, solo provee mensajes emitidos por el Sink
    - rating-events           -> Function -> in-0  -> Este tópico, recibe los mensajes del rating-request-internal los transforma
    - rating-events	          -> Function -> out-0 -> Este tópico, también provee el mensaje transformado


- La comunicación entre los tópicos(rating-request-internal, rating-events) se realiza internamente mediante Kafka
- Solo se requiere poner el mensaje inicial en el tópico(rating-request-internal) mediante el sink:
```
@Bean
public Supplier<Flux<Message<String>>> ratingRequestSupplier(Sinks.Many<Message<String>> sink) {
log.info("Creating rating request supplier");
return () -> sink.asFlux()
.doOnNext(msg -> log.info("Sending msg to kafka rating-request-internal: {}", msg.getPayload()))
.doOnSubscribe(s -> log.info("Subscribing to rating sink"))
.doOnCancel(() -> log.info("Canceling rating sink"));
}
```

Una vez depositado...

rating-request-internal -> Supplier -> provee mensajes del tipo :
```
{ "idRestaurant": "f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7",
  "uuidCustomer": "b1d30e28-bb68-4cfa-86c7-59ad9a1b16db",
  "comment": "The food was excellent and the service was very attentive. KAFKA 2",
  "rating": 5
}
```

rating-events -> Function in-0 -> Recibe:
```
{ "idRestaurant": "f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7",
  "uuidCustomer": "b1d30e28-bb68-4cfa-86c7-59ad9a1b16db",
  "comment": "The food was excellent and the service was very attentive. KAFKA 2",
  "rating": 5
}
```

rating-events -> Function out-0 -> transforma y publica en :
```
{ "idRestaurant": "f3e749e0-9baf-4c5f-8ef8-8d0f68f8a8a7",
  "rating": 5,
  "uuidCustomer": "b1d30e28-bb68-4cfa-86c7-59ad9a1b16db",
  "comment": "The food was excellent and the service was very attentive. KAFKA 2",
  "eventType": "RATING_CREATED",
  "timestamp": 1755733676639
}
```



