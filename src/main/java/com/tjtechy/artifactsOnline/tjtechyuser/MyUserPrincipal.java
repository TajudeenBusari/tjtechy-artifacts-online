package com.tjtechy.artifactsOnline.tjtechyuser;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;


public class MyUserPrincipal implements UserDetails {

  private final TJUser tjUser;

  public MyUserPrincipal(TJUser tjUser) {

    this.tjUser = tjUser;
  }

//override all methods in the UserDetails
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    //the method here is expecting a collection of granted authorities
    //Convert a user's roles from space-delimited string to a list of simpleGrantedAuthority objects.
    //E.g john's roles are stored in a string like "admin user moderator", we need to convert it to a list of GrantedAuthority
    //before conversion, we need to add this "ROLE_" prefix to each role name.
    return Arrays.stream(StringUtils.tokenizeToStringArray(this.tjUser.getRoles(), " "))
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .toList();
  }

  @Override
  public String getPassword() {

    return this.tjUser.getPassword();
  }

  @Override
  public String getUsername() {

   return this.tjUser.getUsername();

  }

  @Override
  public boolean isAccountNonExpired() {

    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {

    return true;
  }

  @Override
  public boolean isEnabled() {

    return this.tjUser.isEnabled();
  }

  //also create a getter for the TJUser
  public TJUser getTjUser() {

    return tjUser;
  }
}



/*
* This class uses the Adapter Pattern to convert TJUser to userDetails expect
* by the authentication provider
* It implements the UserDetail interface where there is
* loadUserByUsername-->
* many methods as implemented above
* Inject TJUser here
* */