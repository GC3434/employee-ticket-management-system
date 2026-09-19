package com.ugc.api_gateway.config;

import com.ugc.api_gateway.service.GatewayRateLimitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoutes() {
        return route("user-service")
                .route(path("/auth/**"), http())
                .route(path("/empNtkt/**"), http())
                .route(path("/admin/**"), http())
                .filter(lb("USER-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> ticketServiceRoutes(
            GatewayRateLimitService rateLimitService) {

        return route("ticket-service")
                .route(path("/tickets/**"), http())
                .filter((request, next) -> {
                    rateLimitService.checkRateLimit();
                    return next.handle(request);
                })
                .filter(lb("TICKET-SERVICE"))
                .build();
    }
}