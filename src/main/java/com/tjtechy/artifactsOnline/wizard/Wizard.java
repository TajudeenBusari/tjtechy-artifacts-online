package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wizard implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  //one wizard can own many artifacts
  //if we save one wizard in the db, all artifacts associated with it will also be saved
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "owner")
  private List<Artifact> artifacts = new ArrayList<>();

  //no arg constructor
  public Wizard() {
  }

  //create getters and setters
  public Integer getId() {

    return id;
  }

  public void setId(Integer id) {

    this.id = id;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  //getter and setter for artifacts
  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(List<Artifact> artifacts) {
    this.artifacts = artifacts;
  }

  //method to add artifact
  public void addArtifact(Artifact artifact) {
    //this ensures by-directional relationship between wizards and artifacts
    artifact.setOwner(this);
    this.artifacts.add(artifact);
  }

  public Integer getNumberOfArtifacts() {

    return this.artifacts.size();
  }

  /*this method will handle removing all artifacts assigned
  // to a particular wizard before deleting wizard*/
  public void removeAllArtifacts(){
    this.artifacts.stream().forEach(artifact -> artifact.setOwner(null));
    this.artifacts = null;
  }

  //method to remove a single artifact
  public void removeArtifact(Artifact artifactToBeAssigned) {
    //remove artifact owner i.e set to null and remove from list of artifacts
    artifactToBeAssigned.setOwner(null);
    this.artifacts.remove(artifactToBeAssigned);
  }
}
