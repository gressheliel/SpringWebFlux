# 06 Eats Hub Catalog & Cache Reactivo con Redis + Paginación

## Dependencias
- implementation 'org.springframework.boot:spring-boot-starter-data-redis-reactive'


## Actualizacion de docker-compose.yml
- Imagen de Redis
- GUI de Redis
- docker-compose up -d

## Implementación de Redis
- Se implementa para los catálogos de restaurantes.
- Como el redis es un <K, V> se recomienda en las apis que reciben un id.
- Excluye la opción de readAll. (Mala práctica)
- Se recomienda cachar en RestaurantBusinessServiceImpl el objeto RestaurantResponse.
  - Por tamaño, porque es el que se devuelve y para no ir hasta el repositorio.

## Componentes
- RedisConfig
  - Configuración de Redis.
  - Serializar y Deserializar caches
  - Agregar métodos : 
    - lettuceConnectionFactory
    - verifyRedisConnection

## Testing Connection Redis
- Levantar la aplicación y verificar en los logs
  - 13:25:06.317 INFO  [restartedMain] c.e.c.RedisConfig - Redis connection Successfully established...

## RedisCommander GUI
- http://localhost:8081/
  - User: admin
  - Password: debuggeandoideas

## Implementacion del Cache, sin interface
- En com.elhg.services.impls.CatalogCacheService
  - Metodos para gestionar el cache de restaurantes
- En com.elhg.services.impls.RestaurantBusinessServiceImpl
  - Se inyecta el CatalogCacheService
  - Se implementa el método getRestaurantByName con cache
  - Se implementa el método getAllRestaurants con paginación
  - Se implementa el método getRestaurantsByCuisine con cache
  - Se implementa el método getRestaurantByCity con cache
  - Se implementa el método getRestaurantByPriceRange con cache

## Implementación de la paginación
- Paginación del catálogo de restaurantes. -> getAllRestaurants
- readAll() => readAll(Integer page, Integer size);
- Se modifica el handler RestaurantCatalogHandler