package com.tjtechy.artifactsOnline.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/users/{userId}");


  @Override
  public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
    //Extract the userId from the request URI: /users/{userId}
    Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
    String userIdFromRequestUri = uriVariables.get("userId");

    //Extract the userId from the Authentication object, which is a Jwt object
    var authentication = authenticationSupplier.get();
    String userIdFromJwt = ((Jwt)authentication.getPrincipal()).getClaim("userId").toString();

    //Check if the user has the role "ROLE_user"
    boolean hasUserRole = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_user"));

    //Check if the user has the role "ROLE_admin"
    boolean hasAdminRole = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_admin"));

    //Compare the two userIds
    boolean userIdMatch = userIdFromRequestUri != null && userIdFromRequestUri.equals(userIdFromJwt);

    return new AuthorizationDecision(hasAdminRole || (hasUserRole && userIdMatch));
  }
}


/*
* the role of the authorization manager is determined whether access should be granted
* or denied.
* The constructor param of the AuthorizationDecision is <boolean granted>
* In this class, we have to check if admin(we don't need to check Id since it is admin) and
* userRole(we have to check Id).
* The UriTemplate has a powerful method called match that return two Ids and put
* them in a map for example: {hotel =1, booking=42}.In our case we need to extract userId
* */