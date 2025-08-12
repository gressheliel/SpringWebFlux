package com.elhg.routes;

import com.elhg.handlers.ReservationHandler;
import com.elhg.handlers.RestaurantCatalogHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class ReservationCrudRouter {

    @Bean
    public RouterFunction<ServerResponse> routesReservation(ReservationHandler reservationHandler) {
        return RouterFunctions.route()
                .path("/reservation",builder -> {
                            builder.POST("", reservationHandler::postReservation)
                                    .GET("/{id}", reservationHandler::getReservationById)
                                    .DELETE("/{id}", reservationHandler::deleteReservation)
                                    .PUT("/{id}", reservationHandler::updateReservation);
                        }).build();
    }

}




