package com.tjtechy.artifactsOnline.tjtechyuser;

import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.tjtechyuser.converter.TJUserToUserDtoConverter;
import com.tjtechy.artifactsOnline.tjtechyuser.converter.UserDtoToTJUserConverter;
import com.tjtechy.artifactsOnline.tjtechyuser.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")

public class UserController {
  private final UserService userService;

  private final TJUserToUserDtoConverter userToUserDtoConverter;
  private final UserDtoToTJUserConverter userDtoToUserConverter;

  public UserController(UserService userService,
                        TJUserToUserDtoConverter userToUserDtoConverter,
                        UserDtoToTJUserConverter userDtoToUserConverter) {
    this.userService = userService;
    this.userToUserDtoConverter = userToUserDtoConverter;
    this.userDtoToUserConverter = userDtoToUserConverter;
  }

  //find all users
  @GetMapping
  public Result findAllUsers(){

    List<TJUser> foundTJUsers = this.userService.findAll();
    //convert foundUsers to Dto
    List<UserDto> foundUserDto = foundTJUsers.stream()
            .map(this.userToUserDtoConverter::convert)
            .collect(Collectors.toList());

    return new Result(true, StatusCode.SUCCESS, "Find All Success", foundUserDto);
  }

  //find a user by Id
  @GetMapping("/{userId}")
  public Result findUserById(@PathVariable Integer userId){
    TJUser foundUser = this.userService.findById(userId);
    //convert foundUser to Dto
    UserDto userDto = this.userToUserDtoConverter.convert(foundUser);

    return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
  }

  //add user
  //why is domain class used instead of the dto?
  @PostMapping
  public Result addUser(@Valid @RequestBody TJUser newTJUser){
    TJUser savedUser = this.userService.save(newTJUser);
    //convert the savedUser to Dto
    UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
    return  new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
  }

  //update user
  @PutMapping("/{userId}")
  public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDto){
    //first convert userDto to TJUser
    TJUser update = this.userDtoToUserConverter.convert(userDto);
    TJUser updatedTJUser = this.userService.update(userId, update);

    //convert back to Dto
    UserDto updatedTJUserDto = this.userToUserDtoConverter.convert(updatedTJUser);

    return new Result(true, StatusCode.SUCCESS, "Update Success", updatedTJUserDto);
  }

  //delete user
  @DeleteMapping("/{userId}")
  public Result deleteUser(@PathVariable Integer userId){
    this.userService.delete(userId);
    return new Result(true, StatusCode.SUCCESS, "Delete Success");
  }
}
