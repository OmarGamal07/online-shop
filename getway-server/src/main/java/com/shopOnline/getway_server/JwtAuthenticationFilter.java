package com.shopOnline.getway_server;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);
        if (userEmail == null) {
            return chain.filter(exchange);
        }

        return userDetailsService.findByUsername(userEmail)
                .flatMap(userDetails -> {
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        // Set authentication in the ReactiveSecurityContext
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                    } else {
                        return chain.filter(exchange);
                    }
                });
    }
}
