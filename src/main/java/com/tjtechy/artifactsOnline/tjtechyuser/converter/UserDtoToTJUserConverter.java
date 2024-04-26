package com.tjtechy.artifactsOnline.tjtechyuser.converter;

import com.tjtechy.artifactsOnline.tjtechyuser.TJUser;
import com.tjtechy.artifactsOnline.tjtechyuser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToTJUserConverter implements Converter<UserDto, TJUser> {
  @Override
  public TJUser convert(UserDto source) {
    TJUser user = new TJUser();
    user.setUsername(source.username());
    user.setEnabled(source.enabled());
    user.setRoles(source.roles());
    return user;
  }
}
