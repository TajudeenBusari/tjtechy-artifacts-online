package com.tjtechy.artifactsOnline.system.exception;

public class ObjectNotFoundException extends RuntimeException{

  public ObjectNotFoundException(String objectName, String id) {
    super("Could not find " + objectName + " with Id " + id);
  }

  public ObjectNotFoundException(String objectName, Integer id) {
    super("Could not find " + objectName + " with Id " + id);
  }
}
/*this class combines or it is a general class
*for wizard and artifact not found exception
*
*
*
 */