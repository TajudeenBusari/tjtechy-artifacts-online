package com.tjtechy.artifactsOnline.security;

import com.tjtechy.artifactsOnline.tjtechyuser.MyUserPrincipal;
import com.tjtechy.artifactsOnline.tjtechyuser.TJUser;
import com.tjtechy.artifactsOnline.tjtechyuser.converter.TJUserToUserDtoConverter;
import com.tjtechy.artifactsOnline.tjtechyuser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

  private final JwtProvider jwtProvider;

  private final TJUserToUserDtoConverter userToUserDtoConverter;

  public AuthService(JwtProvider jwtProvider, TJUserToUserDtoConverter userToUserDtoConverter) {
    this.jwtProvider = jwtProvider;
    this.userToUserDtoConverter = userToUserDtoConverter;
  }


  public Map<String, Object> createLoginInfo(Authentication authentication) {

    //Create user info
    //How do we retrieve user info from authentication?
    MyUserPrincipal principal = (MyUserPrincipal)authentication.getPrincipal();

    //we can then retrieve TJUser (has password info) and convert to dto before sending back to the client
    TJUser tjUser = principal.getTjUser();
    UserDto userDto = this.userToUserDtoConverter.convert(tjUser);

    //Create a JWT
    String token = this.jwtProvider.createToken(authentication);
//    String token = ""; just for test

    Map<String, Object> loginResultMap = new HashMap<>();

    loginResultMap.put("userInfo", userDto);
    loginResultMap.put("token", token);

    //return the loginResultMap to the controller
    return loginResultMap;
  }
}

/*authentication store the myPrincipal details, SO WE CAN USE
getPrincipal and wrap it around our MyUserPrincipal
*
*
*
* */