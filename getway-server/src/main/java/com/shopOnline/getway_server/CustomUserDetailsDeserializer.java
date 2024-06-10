package com.shopOnline.getway_server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetailsDeserializer extends StdDeserializer<CustomUserDetails> {

    public CustomUserDetailsDeserializer() {
        this(null);
    }

    protected CustomUserDetailsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CustomUserDetails deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Long id = node.get("id").asLong();
        String firstname = node.get("firstname").asText();
        String lastname = node.get("lastname").asText();
        String email = node.get("email").asText();
        String password = node.get("password").asText();

        // Deserialize wallet
        JsonNode walletNode = node.get("wallet");
        CustomUserDetails.Wallet wallet = null;
        if (walletNode != null) {
            Long walletId = walletNode.get("id").asLong();
            int balance = walletNode.get("balance").asInt();
            wallet = new CustomUserDetails.Wallet(walletId, balance);
        }

        // Deserialize role
        Role role = Role.valueOf(node.get("role").asText());

        // Deserialize authorities
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        JsonNode authoritiesNode = node.get("authorities");
        if (authoritiesNode != null && authoritiesNode.isArray()) {
            for (JsonNode authorityNode : authoritiesNode) {
                authorities.add(new SimpleGrantedAuthority(authorityNode.get("authority").asText()));
            }
        }

        // Deserialize other fields and construct CustomUserDetails object
        return CustomUserDetails.builder()
                .id(id)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .wallet(wallet)
                .role(role)
                .authorities(authorities)
                .build();
    }
}
