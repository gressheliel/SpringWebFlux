# Dependencias para pruebas unitarias
testImplementation('io.projectreactor:reactor-test:3.7.3')
testImplementation('org.mockito:mockito-junit-jupiter:5.11.0')

# StepVerifier para pruebas unitarias
- Permite testear controladores de flujos reactivos
- Es como un JUnit+Mockito, pero exclusivamente para flujos reactivos

# Mono defer
- Mono.defer se utiliza para diferir la creación de un Mono hasta el momento de la suscripción. 
  - Esto es útil cuando quieres que el código dentro del Mono se ejecute cada vez que alguien se suscriba, 
  - en vez de ejecutarse inmediatamente.
- Deberías usar Mono.defer en lugar de Mono.just cuando necesites diferir la creación del Mono 
- hasta el momento de la suscripción, especialmente si el valor o la lógica que genera 
- el Mono puede cambiar o depende del contexto en el momento de la suscripción

- Casos Comunes :
  - Ejecución diferida: Si el valor depende de una operación que debe ejecutarse en cada suscripción.
  - Evitar valores precomputados: Cuando no quieres que el valor se calcule inmediatamente al crear el Mono.
  - Operaciones dinámicas: Como lecturas de base de datos, llamadas a servicios externos o cálculos que cambian con el tiempo.

// Mono.just: el valor se calcula inmediatamente
String valor = obtenerValor(); // Se ejecuta ahora
Mono<String> monoJust = Mono.just(valor);

// Mono.defer: el valor se calcula al suscribirse
Mono<String> monoDefer = Mono.defer(() -> Mono.just(obtenerValor()));