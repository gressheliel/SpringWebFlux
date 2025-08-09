# 04 Eats Hub Catalog & Router Function

## Mono<ServerResponse> 
- Para garantizar que sea compatible con la mayoría de navegadores, se recomienda usar `Mono<ServerResponse>`.
  - Usa Mono<ServerResponse> para la mayoría de endpoints REST.
  - Usa Flux<ServerResponse> solo si necesitas enviar múltiples respuestas en streaming (por ejemplo, Server-Sent Events)


