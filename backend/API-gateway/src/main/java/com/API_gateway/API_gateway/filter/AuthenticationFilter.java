package com.API_gateway.API_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RoutingValidator routingValidator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            String method = request.getMethod().name();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.getResponse().setStatusCode(HttpStatus.OK);
                return exchange.getResponse().setComplete();
            }

            System.out.println(path);
            System.out.println("From AuthenticationFilter");

            // If the route requires authentication
            if (routingValidator.isSecured.test(request)) {
                System.out.println("Secured route check: TRUE");

                String token = null;

                // 1️⃣ First try to get token from Authorization header
                if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                        System.out.println("Token from header: " + token);
                    }
                }

                // 2️⃣ If not present in header, try from query param
                if (token == null) {
                    token = request.getQueryParams().getFirst("token");
                    if (token != null) {
                        System.out.println("Token from query param: " + token);
                    }
                }

                // 3️⃣ If still no token, return 401
                if (token == null) {
                    System.out.println("No token provided");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String finalAuthHeader = token;
                return webClientBuilder.build()
                        .get()
                        .uri("http://AuthenticationApp/api/users/Validatetoken?Authorization=" + finalAuthHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(role -> {
                            role = role.replace("\"", "").trim();
                            System.out.println("User Role: " + role);

                            // Role-based access control
                            if ((path.startsWith("/api/users/dashboard") ||
                                    path.startsWith("/api/users/role/") ||
                                    path.startsWith("/api/users/id/")||
                                    path.startsWith("/api/owner"))
                                    && ("ADMIN".equals(role) || "MANAGER".equals(role))) {
                                return chain.filter(exchange);

                            } 
                            if ("MANAGER".equals(role)) {
                                if (path.startsWith("/api/plane-allocation") || path.startsWith("/api/hangar-allocation")) {
                                    // full access for MANAGER on allocations
                                    return chain.filter(exchange);
                                }
                                if ((path.startsWith("/api/pilots") ||
                                     path.startsWith("/api/planes") ||
                                     path.startsWith("/api/hangars")) && "GET".equalsIgnoreCase(method)) {
                                    return chain.filter(exchange);
                                }
                            }
                            if ("ADMIN".equals(role)) {
                                if ((path.startsWith("/api/plane-allocation") || path.startsWith("/api/hangar-allocation"))
                                    && "GET".equalsIgnoreCase(method)) {
                                    // Admin can only view allocations (GET)
                                    return chain.filter(exchange);
                                }

                                if (path.startsWith("/api/address") ||
                                		path.startsWith("/api/users")||
                                		path.startsWith("/api/pilots") ||
                                    path.startsWith("/api/planes") ||
                                    path.startsWith("/api/hangars")) {
                                    // Admin full access on users, pilots, planes, hangars
                                    return chain.filter(exchange);
                                }
                            }

                            // If role does not match required permissions
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                        .onErrorResume(error -> {
                            System.out.println("Error validating token: " + error.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            }

            // If route does not require authentication
            System.out.println("Access not required for this route");
            return chain.filter(exchange);
        };
    }
}
