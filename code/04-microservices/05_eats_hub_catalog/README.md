# 05 Eats Hub Catalog & Validaciones, GlobalHandlerException, Router Function

## Dependencias
- implementation 'org.springframework.boot:spring-boot-starter-validation'

## Validaciones
- Agregar anotaciones de validación a los Request DTOs(ReservationRequest)
  - @NotBlank: para cadenas que no deben estar vacías.
  - @Size: para definir un tamaño mínimo y máximo.
  - @Min/@Max: para valores numéricos.
  - @Email: para validar correos electrónicos.
  
- Agregar com.elhg.config.ValidatorConfig
  - Para cerrar el recurso, se puede usar el AutoCloseable de try-with-resources.
```
public  Validator validator(){
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            return validatorFactory.getValidator();
        }
    }
```
  - O usar el @PreDestroy, como esta implementado.

## Para hacer las validaciones reactivas
- Crear ReactiveValidator en com.elhg.validators.ReactiveValidator
- Crear ErrorConstants en com.elhg.constants.ErrorConstants
- Crear GlobalErrorHandler en com.elhg.handlers.GlobalErrorHandler
  - Es equivalente al AdviceControllerAdvice, pero para WebFlux.
- Crear com.elhg.handlers.ReservationHandler
- Crear com.elhg.router.ReservationRouter
  - En el router, se define la ruta y se asocia con el handler.

## Handlers
- Para el caso de los handlers ya no es necesario, buscar por id
  .flatMap(reservationBusinessService::readByReservationId)
  .switchIfEmpty(Mono.error(new ValidationException("Reservation not found with id :( : " + reservationId)))
- Esas validaciones se hacen directamente en el servicio
  - ReservationHandler ->
    ReservationCrudRoutes -> 
    ReservationBusinessService -> 
    ReservationCrudServiceImpl(Aquí se hace la validación)
  

## Casos de prueba con Reservations
- Creación de Reservation
  - Reservation con horario fuera de atención
  - Reservation con ID de restaurante inexistente
  - Reservation con email inválido
  - Reservation con UUID incorrectos(Cliente,Restaurante)
  - Datos de Reservation válidos, crea un Reservation exitosamente y devuelve UUID
- Obtener la reservation por ID
  - Reservation con ID inexistente, devuelve error 404
  - Reservation con ID válido, devuelve la Reservation
- Actualizar la reservation por ID
  - Reservation con ID inexistente, devuelve error 404
- Eliminar la reservation por ID
  - Reservation con ID inexistente, devuelve error 404

