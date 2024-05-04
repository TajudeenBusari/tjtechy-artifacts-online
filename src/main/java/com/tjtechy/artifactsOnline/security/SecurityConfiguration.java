package com.tjtechy.artifactsOnline.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration


public class SecurityConfiguration {

  private final RSAPublicKey publicKey;

  private final RSAPrivateKey privateKey;

  //@Value("{api.endpoint.base-url}")
  private String baseUrl = "/api/v1";

  private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

  private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;

  private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

  //generate a constructor
  public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
                               CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint,
                               CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler)
          throws NoSuchAlgorithmException {
    this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
    this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
    this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;

    //Generate a public/private key pair in java
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048); //generated will have size of 2048 bit
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    this.publicKey = (RSAPublicKey) keyPair.getPublic();
    this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers(HttpMethod.GET,  baseUrl + "/artifacts/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, baseUrl + "/users/**")
                    .hasAuthority("ROLE_admin")//protect the endpoint
                    .requestMatchers(HttpMethod.POST, baseUrl + "/users")
                    .hasAuthority("ROLE_admin")//protect the endpoint
                    .requestMatchers(HttpMethod.PUT, baseUrl + "/users/**")
                    .hasAuthority("ROLE_admin") //protect the endpoint
                    .requestMatchers(HttpMethod.DELETE, baseUrl + "/users/**")
                    .hasAuthority("ROLE_admin")//protect the endpoint
                    .requestMatchers(EndpointRequest.to("health", "info", "prometheus")).permitAll()
                    .requestMatchers(EndpointRequest.toAnyEndpoint().excluding("health", "info", "prometheus")).hasAuthority("ROLE_admin")
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                    .anyRequest().authenticated()//every other thing is authenticated.Always a good idea to put this as last
            )
            //.headers(headers -> headers.frameOptions().disable())//for h2 console browser access
            .headers(headers -> headers.frameOptions().disable())//for h2 console browser access
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            //.httpBasic(Customizer.withDefaults())
            .httpBasic(httBasic -> httBasic.authenticationEntryPoint(this.customBasicAuthenticationEntryPoint))
            .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt().and()
                    .authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                    .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler))

            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//turn off session
            .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder(){

    return new BCryptPasswordEncoder(12);
  }

  //prepare encoder and decoder

  //encoder
  @Bean
  public JwtEncoder jwtEncoder(){
    //JWK --> Json web key
    JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
    JWKSource<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSet);
  }

  //decoder
  @Bean
  public JwtDecoder jwtDecoder(){
    return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter(){

    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); //dont any prefix to my authority name

    JwtAuthenticationConverter jwtAuthenticationConverter =
            new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

}



/*We will use the base url here, so it has to be injected
*Design the builder pattern to customize our security and override the
* default security provided by spring security
* For example, no authentication is needed to findAllArtifacts
* OR findArtifactById
* line 20 means that if method is GET and has the pattern (/api/v1/artifacts/**),
*permit such requests without authentication
*FOR usermanagement:Only user with admin role is permitted to access that end point
*We will make h2 console public as well since we are using it for development by
adding line 31 and line 33
*NB:frameOptions is deprecated and marked for removal as at writing this code
*csrf->Cross-Site Request Forgery has to be disabled else we will have problem accessing, POST and PUT
requests
*
NB: you can't use /artifacts/** after this {api.endpoint.base-url}")
in the new spring security architecture So I have defined my base url from
 * this class rather than from yml file/
 */

/*To check any method definitaion, ctrl spcae bar on the method
hover over the ... and check quick definition
the strength of password encoder is 12
*
*/
/*we define two RSA key to generate the jwt
* Anytime, the application is restarted, ypu have a new pair of keys (public and private)
* */
/**
 * so if you provide a a wrong user or password, you will see a meaningful
 * response instead default unauthorized because we have:
 * Registered the customBasicAuthenticationEntryPoint
 * Regisyter the CustomBearerTokenAuthenticationEntryPoint with the oauth2ResourceServer
 * Register the accessDeniedException
 */