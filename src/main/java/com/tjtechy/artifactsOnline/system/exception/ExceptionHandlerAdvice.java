package com.tjtechy.artifactsOnline.system.exception;

import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

  @ExceptionHandler(ObjectNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  Result handleObjectFoundException(ObjectNotFoundException exception){

    return new Result(false, StatusCode.NOT_FOUND, exception.getMessage());
  }

  //exception handling when any required field is not provided
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  Result handleValidationException(MethodArgumentNotValidException exception){
    List<ObjectError> errors = exception.getBindingResult().getAllErrors();
    Map<String, String> map = new HashMap<>(errors.size());
    errors.forEach((error) -> {
      String key = ((FieldError) error).getField();
      String val = error.getDefaultMessage();
      map.put(key, val);
    });
    return new Result(false, StatusCode.INVALID_ARGUMENT, "provided argument are invalid, see data for details.", map);
  }

  @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  Result handleAuthenticationException(Exception exception){

    return new Result(false, StatusCode.UNAUTHORIZED, "username or password is incorrect", exception.getMessage());
  }

  @ExceptionHandler(AccountStatusException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  Result handleAccountStatusException(AccountStatusException exception){

    return new Result(false, StatusCode.UNAUTHORIZED, "User account is abnormal", exception.getMessage());
  }

  @ExceptionHandler(InvalidBearerTokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  Result handleInvalidBearerTokenException(InvalidBearerTokenException exception){

    return new Result(false, StatusCode.UNAUTHORIZED, "The access token provided is expired, revoked, malformed, or invalid for other reasons.", exception.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)//import class from org.springframework.security.access.AccessDeniedException not from java.nio.file.AccessDeniedException
  @ResponseStatus(HttpStatus.FORBIDDEN)
  Result handleAccessDeniedException(AccessDeniedException exception){

    return new Result(false, StatusCode.FORBIDDEN, "No permission", exception.getMessage());
  }

  /**
   * fallback handles any unhandled exceptions
   * @param exception
   * @return
   */
  //all other unhandled exceptions
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  Result handleOtherException(Exception exception){

    return new Result(false, StatusCode.INTERNAL_SERVER_ERROR, "A server internal error occurs.", exception.getMessage());
  }

}


//we don't to add data in the Result object cos it will  be null
//add annotation to this method to tell spring to know which exception the method iS handling