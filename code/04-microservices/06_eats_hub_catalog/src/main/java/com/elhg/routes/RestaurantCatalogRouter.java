package com.elhg.routes;


import com.elhg.handlers.RestaurantCatalogHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RestaurantCatalogRouter {

    @Bean
    public RouterFunction<ServerResponse> routesRestaurant(RestaurantCatalogHandler restaurantCatalogHandler) {
        return RouterFunctions.route()
                .path("/restaurants",builder ->{
                    builder.GET(BY_NAME_URL, restaurantCatalogHandler::getRestaurantByName)
                            .GET("", request -> {
                                if(request.queryParam("name").isPresent()) {
                                    return restaurantCatalogHandler.getRestaurantByName(request);
                                }else if(request.queryParam("cousinType").isPresent()) {
                                    return restaurantCatalogHandler.getRestaurantByCousinType(request);
                                }else if(request.queryParam("prices").isPresent() ) {
                                    return restaurantCatalogHandler.getRestaurantBetweenRange(request);
                                }else if(request.queryParam("city").isPresent()) {
                                    return restaurantCatalogHandler.getRestaurantByCity(request);
                                }else{
                                    return restaurantCatalogHandler.getAllRestaurants(request);
                                }
                            });
                }).build();
    }

    private final static String BY_NAME_URL = "/{name}";
}
