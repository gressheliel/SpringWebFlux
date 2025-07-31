package com.elhg._03MapFilterReduce;

import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Log
public class MainFilterMapReduceFlatMap {
    public static void main(String[] args) {
        //getVideoGamesWithMoreThan80Sales().subscribe(log::info);
        //getTotalPriceOfVideogamesInDiscount().map(price -> "Total : " + price).subscribe(log::info);
        getAllReviewsComments().subscribe(log::info);
    }

    // Return all videogames names with more than 80 sales
    public static Flux<String> getVideoGamesWithMoreThan80Sales() {
        return Database.getVideogamesFlux()
                .filter(videoGame -> videoGame.getTotalSold() > 80)
                .map(Videogame::getName);
    }

    // Sum all prices of each videogame in discount
    public static Mono<Double> getTotalPriceOfVideogamesInDiscount() {
        return Database.getVideogamesFlux()
                .filter(Videogame::getIsDiscount)
                .map(Videogame::getPrice)
                .reduce(0.0, Double::sum);
    }

    // Return all reviews Comments
    public static Flux<String> getAllReviewsComments() {
        return Database.getVideogamesFlux()
                .flatMap(videogame -> Flux.fromIterable(videogame.getReviews()))
                .map(Review::getComment);
    }
}
