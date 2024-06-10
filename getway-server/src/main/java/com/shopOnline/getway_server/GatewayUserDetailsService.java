package com.shopOnline.getway_server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GatewayUserDetailsService implements ReactiveUserDetailsService {

    private final WebClient webClient;

    public GatewayUserDetailsService(WebClient.Builder webClientBuilder, @Value("${wallet.service.url}") String walletServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(walletServiceUrl).build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return webClient.get()
                .uri("/auth/users/email/{username}", username)
                .retrieve()
                .bodyToMono(CustomUserDetails.class)
                .map(customUserDetails -> {


                    UserDetails userDetails = User.builder()
                            .username(customUserDetails.getUsername())
                            .password(customUserDetails.getPassword())
                            .authorities(customUserDetails.getAuthorities()) // Pass the SimpleGrantedAuthority list
                            .accountExpired(!customUserDetails.isAccountNonExpired())
                            .accountLocked(!customUserDetails.isAccountNonLocked())
                            .credentialsExpired(!customUserDetails.isCredentialsNonExpired())
                            .disabled(!customUserDetails.isEnabled())
                            .build();
                    return userDetails;
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }
}
