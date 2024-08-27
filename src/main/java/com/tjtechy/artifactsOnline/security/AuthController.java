package com.tjtechy.artifactsOnline.security;

import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "CRUD REST API for Authentication",
        description = "CRUD REST API - Login to create authentication token"
)
@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class AuthController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  //define handler methods to handle login api end points

  //post

  @Operation(
          summary = "Get User Login info REST API",
          description = "Create Login info"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PostMapping("/login")
  public Result getLoginInfo(Authentication authentication){
    LOGGER.debug("Authenticated user: '{}'", authentication.getName());
    return new Result(true, StatusCode.SUCCESS, "User Info and JSON Web Token", this.authService.createLoginInfo(authentication));
  }

}
/*the controller serialize the map into json string*/