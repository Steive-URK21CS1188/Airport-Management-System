package com.API_gateway.API_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RoutingValidator {

    private static final List<String> openApiEndpoints = List.of(
            "/api/users/login","/api/users/register"
    );

    public Predicate<ServerHttpRequest> isSecured = request ->
            openApiEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
