package com.tjtechy.artifactsOnline.system.exception;

import com.tjtechy.artifactsOnline.artifact.ArtifactNotFoundException;
import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

  @ExceptionHandler(ArtifactNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  Result handleArtifactNotFoundException(ArtifactNotFoundException exception){

    return new Result(false, StatusCode.NOT_FOUND, exception.getMessage());
  }

  //exception handling when any required field is not privided
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  Result handleValidationException(MethodArgumentNotValidException ex){
    List<ObjectError> errors = ex.getBindingResult().getAllErrors();
    Map<String, String> map = new HashMap<>(errors.size());
    errors.forEach((error) -> {
      String key = ((FieldError) error).getField();
      String val = error.getDefaultMessage();
      map.put(key, val);
    });
    return new Result(false, StatusCode.INVALID_ARGUMENT, "provided argument are invalid, see data for details.", map);

  }
}


//we don't to add data in the Result object cos it will  be null
//add annotation to this method to tell spring to know which exception the method iS handling