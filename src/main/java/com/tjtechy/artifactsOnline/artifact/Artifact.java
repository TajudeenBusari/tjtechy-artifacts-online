package com.tjtechy.artifactsOnline.artifact;

import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import com.tjtechy.artifactsOnline.wizard.Wizard;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Artifact implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;



  private String name;



  private String description;


  private String imageUrl;

  //one owner can own zero to many artifacts
  @ManyToOne
  private Wizard owner;

  //create No arg constructor
  public Artifact() {
  }

  //generate getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  //getter and setter for owner
  public Wizard getOwner() {
    return owner;
  }

  public void setOwner(Wizard owner) {
    this.owner = owner;
  }
}

//Artifact is the many side