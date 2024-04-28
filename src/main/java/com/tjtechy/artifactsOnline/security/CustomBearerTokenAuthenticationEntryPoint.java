package com.tjtechy.artifactsOnline.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

/**
 * This class handles unsuccessful JWT authentication.
 */
@Component
public class CustomBearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
  /**
   * we have injected the defaultHandlerExceptionResolver and delegate the handler to this resolver
   * This security exception can now be handle with controller advice with an exception handler method
   */

  private final HandlerExceptionResolver resolver;

  public CustomBearerTokenAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    //add header to the http response
    //response.addHeader("WWW-Authenticate", "Basic realm=\"Realm\"");
    this.resolver.resolveException(request, response, null, authException);

  }
}
