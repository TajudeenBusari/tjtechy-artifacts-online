package com.tjtechy.artifactsOnline.tjtechyuser;

import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.tjtechyuser.converter.TJUserToUserDtoConverter;
import com.tjtechy.artifactsOnline.tjtechyuser.converter.UserDtoToTJUserConverter;
import com.tjtechy.artifactsOnline.tjtechyuser.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(
        name = "CRUD REST APIs for user Resource",
        description = "CRUD REST APIs - Create User, Update User, Get All Users, Get User, Delete User"
)
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
  @Operation(
          summary = "Get All Users REST API",
          description = "Get All Users is used to retrieve all users from database"
  )
  @ApiResponse(
          responseCode = "201",
          description = "HTTP Status 201 CREATED"
  )
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
  @Operation(
          summary = "Get User by Id REST API",
          description = "Get User by Id is used to get a single user from database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @GetMapping("/{userId}")
  public Result findUserById(@PathVariable Integer userId){
    TJUser foundUser = this.userService.findById(userId);
    //convert foundUser to Dto
    UserDto userDto = this.userToUserDtoConverter.convert(foundUser);

    return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
  }

  //add user
  //why is domain class used instead of the dto?
  @Operation(
          summary = "Create User REST API",
          description = "Create User is used to save user in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PostMapping
  public Result addUser(@Valid @RequestBody TJUser newTJUser){
    TJUser savedUser = this.userService.save(newTJUser);
    //convert the savedUser to Dto
    UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
    return  new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
  }

  //update user
  @Operation(
          summary = "Update a User by Id REST API",
          description = "Update a User is used to change user data in the database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 Ok"
  )
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
  @Operation(
          summary = "Delete a User by Id REST API",
          description = "Delete a User  by Id is used to remove user from the database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 Ok"
  )
  @DeleteMapping("/{userId}")
  public Result deleteUser(@PathVariable Integer userId){
    this.userService.delete(userId);
    return new Result(true, StatusCode.SUCCESS, "Delete Success");
  }
}
