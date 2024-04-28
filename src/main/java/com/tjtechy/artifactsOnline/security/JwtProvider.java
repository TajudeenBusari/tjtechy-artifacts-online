package com.tjtechy.artifactsOnline.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

  private final JwtEncoder jwtEncoder;

  public JwtProvider(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }


  public String createToken(Authentication authentication) {
    //how to create a json web token??
    Instant now = Instant.now();
    long expiresIn = 2; //in 2 hours

    //prepare a claim called authorities
    String authorities = authentication.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .collect(Collectors.joining(" "));//must be space-delimited

    //create claims
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(expiresIn, ChronoUnit.HOURS))
            .subject(authentication.getName())
            .claim("authorities", authorities) //custom custom
            .build();
    //encode claim
    return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}

//it can also be annotated with service as well
//private key for encoding
//public key for decoding