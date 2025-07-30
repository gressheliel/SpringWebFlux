# Pattern Design Observer
- ReactiveStream 
  - Cumple la función de Publisher.
  - Es el Observable que emite los datos. 
- Subscriber
  - Es el Observer que recibe los datos.

- Se crean 2 Publicadores:
  - Publisher1: Emite números del 1 al 10.
  - Publisher2: Emite Strings

- Se crean 2 Subscribers:
  - Subscriber1: Imprime los números recibidos.
  - Subscriber2: Imprime los Strings recibidos.

- Los Publicadores emiten datos y los Subscribers los reciben y procesan.
  

# Processar las annotations de Lombok
- annotationProcessor("org.projectlombok:lombok:1.18.36").
