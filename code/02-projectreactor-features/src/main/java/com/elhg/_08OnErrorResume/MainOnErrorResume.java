package com.elhg._08OnErrorResume;

import com.elhg._03MapFilterReduce.Console;
import com.elhg._03MapFilterReduce.Database;
import com.elhg._03MapFilterReduce.Videogame;
import reactor.core.publisher.Flux;

public class MainOnErrorResume {

    public static void main(String[] args) {
        //handleError().subscribe(videogame -> System.out.println("[subscribe] Received videogame: " + videogame));
        //handleErrorEmitOriginal().subscribe(videogame -> System.out.println("[subscribe] Received videogame: " + videogame));
        handleErrorNoRepeat().subscribe(videogame -> System.out.println("[subscribe] Received videogame: " + videogame));
    }

    //Si se lanza una excepcion, se maneja y se emite un flujo alternativo
    //Reemplaza el flujo original con un flujo alternativo
    public static Flux<Videogame> handleError() {
        return Database.getVideogamesFlux().handle((vg, sink) -> {
            if (Console.DISABLED == vg.getConsole() ) {
                sink.error(new RuntimeException("VideoGame DISABLED: " + vg.getName()));
                return;
            } else {
                sink.next(vg);
            }
        }).onErrorResume(error -> {
            System.out.println("Error detectado");
            return Database.fluxAssassinsDefault;
        }).cast(Videogame.class);
    }

    // Opcion en la que se continua emitiendo el flujo original
    // Imprime :
    /*
    [subscribe] Received videogame: Videogame(name=Forza Horizon 5, price=50.06, console=XBOX, reviews=[Review(comment=Gr�ficos impresionantes y mundo abierto asombroso, score=5), Review(comment=Un poco costoso, pero vale la pena, score=4), Review(comment=Excelente experiencia de conducci�n, score=5)], officialWebsite=https://www.forzamotorsport.net, isDiscount=false, totalSold=80)
    [subscribe] Received videogame: Videogame(name=Resident Evil 4, price=55.2, console=ALL, reviews=[Review(comment=Un cl�sico de terror y acci�n, score=5), Review(comment=Versi�n renovada muy bien lograda, score=4), Review(comment=Jugabilidad adictiva, score=5), Review(comment=Excelente rejugabilidad, score=5)], officialWebsite=https://www.residentevil.com/re4, isDiscount=true, totalSold=96)
    Error detectado
    [subscribe] Received videogame: Videogame(name=Forza Horizon 5, price=50.06, console=XBOX, reviews=[Review(comment=Gr�ficos impresionantes y mundo abierto asombroso, score=5), Review(comment=Un poco costoso, pero vale la pena, score=4), Review(comment=Excelente experiencia de conducci�n, score=5)], officialWebsite=https://www.forzamotorsport.net, isDiscount=false, totalSold=80)
    [subscribe] Received videogame: Videogame(name=Resident Evil 4, price=55.2, console=ALL, reviews=[Review(comment=Un cl�sico de terror y acci�n, score=5), Review(comment=Versi�n renovada muy bien lograda, score=4), Review(comment=Jugabilidad adictiva, score=5), Review(comment=Excelente rejugabilidad, score=5)], officialWebsite=https://www.residentevil.com/re4, isDiscount=true, totalSold=96)
    ...
    */
    public static Flux<Videogame> handleErrorEmitOriginal() {
        return Database.getVideogamesFlux().handle((vg, sink) -> {
            if (Console.DISABLED == vg.getConsole() ) {
                sink.error(new RuntimeException("VideoGame DISABLED: " + vg.getName()));
                return;
            } else {
                sink.next(vg);
            }
        }).onErrorResume(error -> {
            System.out.println("Error detectado");
            return Flux.merge(Database.getVideogamesFlux(), Database.fluxAssassinsDefault);
        }).cast(Videogame.class);
    }

    // No repetidos
    /*
    [subscribe] Received videogame: Videogame(name=Forza Horizon 5, price=50.06, console=XBOX, reviews=[Review(comment=Gr�ficos impresionantes y mundo abierto asombroso, score=5), Review(comment=Un poco costoso, pero vale la pena, score=4), Review(comment=Excelente experiencia de conducci�n, score=5)], officialWebsite=https://www.forzamotorsport.net, isDiscount=false, totalSold=80)
    [subscribe] Received videogame: Videogame(name=Resident Evil 4, price=55.2, console=ALL, reviews=[Review(comment=Un cl�sico de terror y acci�n, score=5), Review(comment=Versi�n renovada muy bien lograda, score=4), Review(comment=Jugabilidad adictiva, score=5), Review(comment=Excelente rejugabilidad, score=5)], officialWebsite=https://www.residentevil.com/re4, isDiscount=true, totalSold=96)
    Error detectado
    [subscribe] Received videogame: Videogame(name=Assassin's Creed Origins, price=15.55, console=DISABLED, reviews=[Review(comment=Ambientaci�n en Egipto espectacular, score=5)], officialWebsite=https://www.assassinscreed.com/origins, isDiscount=false, totalSold=65)
    [subscribe] Received videogame: Videogame(name=Assassin's Creed Odyssey, price=15.55, console=DISABLED, reviews=[Review(comment=Entorno griego inmersivo, score=5), Review(comment=Historia extensa, score=5), Review(comment=Misiones secundarias muy divertidas, score=5)], officialWebsite=https://www.assassinscreed.com/odyssey, isDiscount=false, totalSold=33)
    [subscribe] Received videogame: Videogame(name=Assassin's Creed Valhalla, price=30.33, console=DISABLED, reviews=[Review(comment=Gran mejora en el sistema de combate, score=4), Review(comment=La ambientaci�n vikinga es genial, score=3), Review(comment=Mapa muy amplio y detallado, score=4), Review(comment=Excelente banda sonora, score=5)], officialWebsite=https://www.assassinscreed.com/valhalla, isDiscount=false, totalSold=78)
    ...
     */
    public static Flux<Videogame> handleErrorNoRepeat() {
        return Database.getVideogamesFlux().handle((vg, sink) -> {
            if (Console.DISABLED == vg.getConsole() ) {
                sink.error(new RuntimeException("VideoGame DISABLED: " + vg.getName()));
                return;
            } else {
                sink.next(vg);
            }
        }).onErrorResume(error -> {
            System.out.println("Error detectado");
            return Flux.merge(Database.getVideogamesFlux(), Database.fluxAssassinsDefault);
        }).cast(Videogame.class)
            .distinct(Videogame::getName); // Elimina los repetidos por nombre
    }


}
