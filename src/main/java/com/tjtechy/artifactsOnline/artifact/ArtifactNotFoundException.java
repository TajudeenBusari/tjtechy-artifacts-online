package com.tjtechy.artifactsOnline.artifact;

public class ArtifactNotFoundException extends RuntimeException{

  //create a constructor
  public ArtifactNotFoundException(String id){
    super("Could not find artifact with Id " + id);
  }
}
