package com.tjtechy.artifactsOnline.tjtechyuser.converter;

import com.tjtechy.artifactsOnline.tjtechyuser.TJUser;
import com.tjtechy.artifactsOnline.tjtechyuser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TJUserToUserDtoConverter implements Converter<TJUser, UserDto> {
  @Override
  public UserDto convert(TJUser source) {
    UserDto userDto = new UserDto(source.getId(),
                                  source.getUsername(),
                                  source.isEnabled(),
                                  source.getRoles());

    return userDto;
  }
}
//we are bot setting password in Dto