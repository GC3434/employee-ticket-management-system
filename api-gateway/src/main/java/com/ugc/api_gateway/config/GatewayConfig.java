package com.ugc.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoutes() {
        return route("user-service")
                .route(path("/auth/**"), http())
                .filter(lb("USER-SERVICE"))
                .build();
    }
    /*public RouterFunction<ServerResponse> userServiceRoutes() {
        return route("user-service")
                .route(path("/auth/**"), http())
                //.before(uri("http://localhost:8081")) before Eureka
                .before(uri("lb://USER_SERVICE")) //after eureka
                .build();
    }*/


    @Bean
    public RouterFunction<ServerResponse> ticketServiceRoutes(){
        return route("ticket-service")
                .route(path("/tickets/**"),http())
                .filter(lb("TICKET-SERVICE"))
                .build();
    }
    /*public RouterFunction<ServerResponse> ticketServiceRoutes() {
        return route("ticket-service")
                .route(path("/tickets/**"), http())
                //.before(uri("http://localhost:8082")) before eureka
                .before(uri("lb://TICKET_SERVICE")) //after eureka
                .build();
    }*/
}