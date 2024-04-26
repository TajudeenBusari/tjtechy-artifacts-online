package com.tjtechy.artifactsOnline.tjtechyuser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

@Entity
public class TJUser implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @NotEmpty(message = "username is required")
  private String username;

  @NotEmpty(message = "password is required")
  private String password;

  private boolean enabled;

  @NotEmpty(message = "roles are required")
  private String roles; //space separated string e.g admin user

  public TJUser() {
  }

  public TJUser(Integer id, String username, String password, boolean enabled, String roles) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.roles = roles;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  public TJUser(Integer id, String username, boolean enabled, String roles) {
    this.id = id;
    this.username = username;
    this.enabled = enabled;
    this.roles = roles;
  }
}
