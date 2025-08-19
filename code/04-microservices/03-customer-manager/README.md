## 03-customer-manager Security JWT

## Importar librerías de seguridad y JWT
    //Spring Security
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.security:spring-security-test'

    //JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
    runtimeOnly  'io.jsonwebtoken:jjwt-impl:0.12.6'
    runtimeOnly  'io.jsonwebtoken:jjwt-jackson:0.12.6'

## Componentes
- SecurityConfig
    - Configuración de seguridad, incluyendo CORS y CSRF.
    - Configuración de rutas públicas y privadas.
    - Configuración de autenticación y autorización.
- JwtHelper
    - Generación y validación de tokens JWT.
    - Métodos para obtener el usuario desde el token.

- JwtAuthenticationServerConverter
    - Conversión de la autenticación JWT a un objeto de autenticación de Spring Security.
    - Regresa un `Mono<Authentication>`.

- JwtReactiveAuthenticationManager
    - Manejo de la autenticación reactiva.
    - Utiliza `JwtHelper` para validar el token y obtener el usuario.

- CustomUserDetailsService
    - Implementación de `ReactiveUserDetailsService`.
    - Carga el usuario desde la base de datos usando `UserRepository`.
    - Regresa un `Mono<UserDetails>`.

- AuthService
    - Servicio para manejar la autenticación.
    - Utiliza `JwtHelper` para generar tokens JWT.
  
- AuthController
    - Controlador REST para manejar las solicitudes de autenticación.

- RatingController
    - Controlador REST para manejar las solicitudes de calificaciones.
    - Utiliza `RatingService` para manejar la lógica de negocio.
    - Se va utilizar con Kafka