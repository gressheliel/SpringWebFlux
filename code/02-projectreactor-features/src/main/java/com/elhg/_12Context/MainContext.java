package com.elhg._12Context;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/*
    Context Example
    This example demonstrates how to use Reactor's Context to pass user-specific data
    to a filter operation in a reactive stream. The context is used to determine which
    video games to filter based on the user's ID, allowing for dynamic filtering
 */



@Slf4j
public class MainContext {

    // Contexto es como un mapa que se puede usar para pasar información adicional (K, V)
    // contextWrite(Context.of("userId", "10020192"))   Agrega un contexto con el userId. SIEMPRE VA ANTES DEL SUBSCRIBE
    // filterWhen permite filtrar elementos de un Flux o Mono de manera asíncrona, utilizando el contexto


    public static void main(String[] args) {
        Database.getVideogamesFlux()
                .filterWhen(videogame -> Mono.deferContextual(ctx->{
                    var userId = ctx.getOrDefault("userId", "0");
                    if(userId.startsWith("1")){
                        return Mono.just(videoGameForConsole(videogame, Console.XBOX));
                    }else if (userId.startsWith("2")){
                        return Mono.just(videoGameForConsole(videogame, Console.PLAYSTATION));
                    }
                    return Mono.just(false);
                }))
                .contextWrite(Context.of("userId", "10020192"))
                .subscribe(videogame -> log.info("VideoGame: " + videogame.getName() + " - Console: " + videogame.getConsole()));
    }

    // Userid 1- XBOX
    // Userid 2- PLAYSTATION
    // Método que valida el videojuego por tipo de consola
    private static  boolean videoGameForConsole(Videogame videoGame, Console console) {
        return videoGame.getConsole()==console || videoGame.getConsole()==Console.ALL;
    }
}
