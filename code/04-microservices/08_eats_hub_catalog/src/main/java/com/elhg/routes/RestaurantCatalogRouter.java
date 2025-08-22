package com.elhg.routes;


import com.elhg.dtos.responses.RestaurantResponse;
import com.elhg.handlers.RestaurantCatalogHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Restaurants", description = "This is a catalog for restaurants")
@Configuration
public class RestaurantCatalogRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/restaurants/{name}",
                    beanClass = RestaurantCatalogHandler.class,
                    beanMethod = "getRestaurantByName",
                    operation = @Operation(
                            operationId = "getRestaurantByName",
                            summary = "Find restaurant by name",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "name", description = "Restaurant name")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Restaurant found",
                                            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/restaurants",
                    beanClass = RestaurantCatalogHandler.class,
                    beanMethod = "getAllRestaurants",
                    operation = @Operation(
                            operationId = "getAllRestaurants",
                            summary = "List all restaurants",
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
                                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page size")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List of restaurants",
                                            content = @Content(schema = @Schema(implementation = RestaurantResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/restaurants",
                    beanClass = RestaurantCatalogHandler.class,
                    beanMethod = "getRestaurantsByCousinType",
                    operation = @Operation(
                            operationId = "getRestaurantsByCousinType",
                            summary = "Get restaurants by cuisine type",
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "cousinType", description = "Cuisine type", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List by cuisine type",
                                            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/restaurants",
                    beanClass = RestaurantCatalogHandler.class,
                    beanMethod = "getRestaurantBetweenPrice",
                    operation = @Operation(
                            operationId = "getRestaurantBetweenPrice",
                            summary = "Get restaurants by price range",
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "prices", description = "Price levels (comma separated)", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List by price range",
                                            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/restaurants",
                    beanClass = RestaurantCatalogHandler.class,
                    beanMethod = "getRestaurantsByCity",
                    operation = @Operation(
                            operationId = "getRestaurantsByCity",
                            summary = "Get restaurants by city",
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "city", description = "City name", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List by city",
                                            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
                                    @ApiResponse(responseCode = "204", description = "No content"),
                                    @ApiResponse(responseCode = "400", description = "Bad request")
                            }
                    )
            )
    })
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
