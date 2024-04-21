package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wizard implements Serializable {

  @Id
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
}
