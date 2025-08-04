# Conexión con MongoDB en Compass
- ...\04-microservices\docker-compose up -d
- mongodb://localhost:27017/catalog?authSource=admin
- user : master  
- password : debuggeandoideas
  
# Conexión con MongoDB con properties
#spring.data.mongodb.host=localhost
#spring.data.mongodb.port=27017
#spring.data.mongodb.username=master
#spring.data.mongodb.password=debuggeandoideas
#spring.data.mongodb.database=catalog
#spring.data.mongodb.authentication-database=admin
#spring.data.mongodb.auto-index-creation=true

# Conexión con MongoDB con Bean
- Configuración en la clase MongoConfig.java

# Records Java
- Son inmutables, no se pueden modificar una vez creados.
- Solo tienen getters, no setters.
- Mas ligeros que las clases normales.
- Se pueden crear de forma más sencilla.

# Prueba intermedia con  Repo extends ReactiveMongoRepository<RestaurantCollection, UUID>
- Se puede usar el Repo desde el Main para hacer un test y recuperar los datos de la base de datos.

# Querys en MongoDB
db.getCollection("restaurants").find({ "cuisineType": "Japanese" }).pretty()
db.restaurants.find({ "cuisineType": "Japanese" })
db.restaurants.find({"name": {"$regex": "La", "$options": "i"}})
db.restaurants.find({"priceRange":{$in: ["CHEAP", "MEDIUM"]}})
db.restaurants.find({"address.city": "New York"})

# flatMap flatMapMany
- flatMap combina 2 Mono/Flux en uno solo
  - Mono<RestaurantCollection> findById(ID id)  => Mono<ReservationCollection> createReservation(ReservationCollection reservation);
  - Mono<Mono<ReservationCollection>> no es lo que queremos, queremos un Mono<ReservationCollection>
- flatMapMany se usa para transformar un Mono en un Flux
  - Mono<RestaurantCollection> findById(ID id)  => Flux<T> findByRestaurantId(UUID restaurantId);
