## 02-customer-manager R2DBC POSGRESQL
- R2DBC no tiene relaciones @OneToMany, @ManyToOne, etc.
- Se ejecutan a base de querys SQL


## Si el servicio de postegresql esta corriendo, detenerlo
systemctl stop postgresql

## Agregar a docker-compose.yml
- Sección de la base de datos de postgresql

# Agregar los scripts
- Van numerados
- 01-init.sql y 02-data.sql

## Agregar dependencias al project
    implementation 'org.springframework.data:spring-data-relational'
    implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
    runtimeOnly 'org.postgresql:r2dbc-postgresql'
    runtimeOnly 'io.r2dbc:r2dbc-pool'

## Configuración de base de datos, pool de conexiones y logging
- Editar el archivo application.properties

## Crear Tables
- Crear las CustomerRoleTable.java, RoleTable.java y CustomerTable.java

## Creación de Repositories
- Crear los repositorios CustomerRepository.java y RoleRepository.java
- CustomerRoleRepository no tiene llave primaria.
  - Se pone como : CustomerRoleRepository extends R2dbcRepository<CustomerRoleTable, Void> 

## Crear los servicios
- Crear los servicios CustomerService.java

## Crear los controladores
- CustomerController.java

