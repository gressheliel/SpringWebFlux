## 05 Swagger Customer Manager
- En SecurityConfig agregar .pathMatchers(SWAGGER_WHITELIST).permitAll()
- Agregar lista de SWAGGER_WHITELIST

## Dependencias
implementation 'org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.9'

## Configuración
- Configurar en el properties:
```
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui
    use-root-path: true
```

## Componentes
- Crear clase OpenApiConfig
- Revisar ReservationController, con las anotaciones de Swagger

## URLs
- http://localhost:9090/customer/api-docs
- http://localhost:9090/customer/swagger-ui/index.html
- 