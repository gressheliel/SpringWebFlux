# 02 Eats Hub Catalog & Spring Validation Reactive

## Se modifica la BD de MongoDB para incluir los campos de validación
- Agregar campo closeAt en RestaurantCollection
- private String closeAt;

## Simular una llamada a un cliente reactivo para validar una reservación
- PlannerMSClient.java

## Mono.fromCallable vs Mono.fromRunnable
- Mono.fromCallable ejecuta una función que retorna un valor y emite ese valor como resultado del Mono.
  - Mono<String> mono1 = Mono.fromCallable(() -> "resultado");
- Mono.fromRunnable ejecuta una tarea que no retorna valor; el Mono solo emite una señal de finalización (Mono<Void>).
  - Mono<Void> mono2 = Mono.fromRunnable(() -> System.out.println("Ejecutado"));
Usa fromCallable si necesitas emitir un valor, y fromRunnable si solo te interesa la finalización de la tarea.

## Para las validaciones
- BusinessException.java  
  - Exception personalizada para manejar errores de negocio.
- BusinessValidator.java
  - Definición de la interface funcional
  - Cada método validate del BusinessValidator(Interface funcional)
    retorna un Mono<Void> para indicar que la validación se ha completado sin errores.
  - En caso de error, se lanza una BusinessException con un mensaje específico.
- ReservationValidator.java
  - Implementación concreta de la validación de reservas de restaurantes.
  - Cada método de ReservationValidator devuelve un BusinessValidator<ReservationCollection> 
  - La expresión lambda es la implementación del método validate de BusinessValidator.
  - reservation -> { ...}
  - public <T>Mono<Void> applyValidations(T input, List<BusinessValidator<T>> validations){
    - <T>Mono<Void>  
    - <T> : Definicion del tipo generico T
    - Mono<Void> : Retorna un Mono que indica que la validación se ha completado sin errores.

## Ejemplo de implementación de un BusinessValidator para validar reservas, Sin expresiones lambda
```
public BusinessValidator<ReservationCollection> validateMethod() {
        BusinessValidator<ReservationCollection> businessValidator = new BusinessValidator<ReservationCollection>() {
            @Override
            public Mono<Void> validate(ReservationCollection input) {
                return Mono.empty();
            }
        };
        
        return businessValidator;
    }
```

## Pattern de diseño Chain of Responsibility
```
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.stream().reduce(0, (sum, number) -> sum + number);
Imprime: 15
```

- Para el reduce
  - Mono.empty(), valor inicial de la cadena de validaciones.
  - (chain, validator) -> chain.then(validator.validate(input)),
  - chain y validator son del mismo tipo, ambos son BusinessValidator<T>.
  - chain es el resultado acumulado de las validaciones previas.
  - validator.validate(input) es la validación actual que se aplica al input.
  - Mono::then, La cadena de validaciones se ejecuta secuencialmente
  
```
public <T>Mono<Void> applyValidations(T input, List<BusinessValidator<T>> validations){
        if (validations == null || validations.isEmpty()) {
            log.warn("No validations provided, skipping validation.");
            return Mono.empty();
        }
        //Mono::then  Ejecuta las validaciones secuencialmente.
        return validations.stream()
                .reduce(Mono.empty(), (chain, validator) ->
                        chain.then(validator.validate(input)), Mono::then);

    }
```

## then VS thenReturn
- then: Se usa para encadenar una operación que se ejecuta después de que el Mono anterior se completa.
- thenReturn: Se usa para devolver un valor específico cuando el Mono anterior se completa, sin importar su resultado.
