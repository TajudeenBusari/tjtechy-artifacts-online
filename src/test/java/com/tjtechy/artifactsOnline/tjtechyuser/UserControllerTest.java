package com.tjtechy.artifactsOnline.tjtechyuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import com.tjtechy.artifactsOnline.tjtechyuser.dto.UserDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  UserService userService;

  @Autowired
  ObjectMapper objectMapper;

  List<TJUser> users;

  //inject the base url from application.yml file
//  @Value("${api.endpoint.base-url}")
  @Value("/api/v1")
  String baseUrl;

  @BeforeEach
  void setUp() {
    this.users = new ArrayList<>();

    TJUser user1 = new TJUser();
    user1.setId(1);
    user1.setUsername("john");
    user1.setRoles("admin user");
    user1.setPassword("123456");
    user1.setEnabled(true);
    //add user1 to list
    this.users.add(user1);

    TJUser user2 = new TJUser();
    user2.setId(2);
    user2.setUsername("eric");
    user2.setRoles("user");
    user2.setPassword("654321");
    user2.setEnabled(true);
    //add user2 to list
    this.users.add(user2);

    TJUser user3 = new TJUser();
    user3.setId(3);
    user3.setUsername("tom");
    user3.setRoles("user");
    user3.setPassword("qwerty");
    user3.setEnabled(false);
    //add user3 to list
    this.users.add(user3);

  }

  @AfterEach
  void tearDown() {
  }

  //1. Find all users
  @Test
  void testFindAllUsersSuccess() throws Exception {
    //Given
    given(this.userService.findAll()).willReturn(this.users);

    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].username").value("john"))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].username").value("eric"));
  }

  //2. Find user by id
  //positive scenario
  @Test
  void testFindUserByIdSuccess() throws Exception {
    //Given
    given(this.userService.findById(2)).willReturn(this.users.get(1));

    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value(2))
            .andExpect(jsonPath("$.data.username").value("eric"));
  }

  //negative scenario
  @Test
  void testFindUserByIdNotFound() throws Exception {
    //Given
    given(this.userService.findById(5)).willThrow(new ObjectNotFoundException("user", 5));

    //When and //Then
    this.mockMvc.perform(get(this.baseUrl + "/users/5").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //3. add user
  @Test
  void testAddUserSuccess() throws Exception {
    //Given
    //create a Dto obj and covert to json string using Jackson before sending
    //jackson must be autowired into this test class
    //define a fake data that will be returned by artifactService.save()
    TJUser user = new TJUser();
    user.setId(4);
    user.setUsername("lily");
    user.setPassword("123456");
    user.setEnabled(true);
    user.setRoles("admin user"); //the delimiter is the space between admin and user

    //UserDto userDto = new UserDto(null, "lily", true, "admin user");
    String json = this.objectMapper.writeValueAsString(user);

    given(this.userService.save(Mockito.any(TJUser.class))).willReturn(user);

    //When and Then
    this.mockMvc.perform(post(this.baseUrl + "/users").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Add Success"))
            .andExpect(jsonPath("$.data.id").isNotEmpty())
            .andExpect(jsonPath("$.data.username").value(user.getUsername()))
            .andExpect(jsonPath("$.data.enabled").value(user.isEnabled()))
            .andExpect(jsonPath("$.data.roles").value(user.getRoles()));

  }

  //3. update user
  //positive scenario
  @Test
  void testUpdateUserSuccess() throws Exception {
    //Given
    UserDto userDto = new UserDto(3, "tom123", false, "user");

    //data to update
    TJUser updatedUser = new TJUser();
    updatedUser.setId(3);
    updatedUser.setUsername("tom123");
    updatedUser.setEnabled(false);
    updatedUser.setRoles("user");

    String json = this.objectMapper.writeValueAsString(userDto);
    given(this.userService.update(eq(3), Mockito.any(TJUser.class))).willReturn(updatedUser);

    //When and Then
    this.mockMvc.perform(put(this.baseUrl + "/users/3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Update Success"))
            .andExpect(jsonPath("$.data.id").value(3))
            .andExpect(jsonPath("$.data.username").value(updatedUser.getUsername()))
            .andExpect(jsonPath("$.data.enabled").value(updatedUser.isEnabled()))
            .andExpect(jsonPath("$.data.roles").value(updatedUser.getRoles()));
  }

  //negative scenario
  @Test
  void testUpdateUserErrorWithNonExistingId() throws Exception {
    //Given
    given(this.userService.update(eq(5), Mockito.any(TJUser.class)))
            .willThrow(new ObjectNotFoundException("user", 5));
    UserDto userDto = new UserDto(5, "tom123", false, "user");

    String json = this.objectMapper.writeValueAsString(userDto);

    //When and Then
    this.mockMvc.perform(put(this.baseUrl + "/users/5")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //4. delete user
  //positive scenario
  @Test
  void testDeleteUserSuccess() throws Exception {
    //Given
    doNothing().when(this.userService).delete(2);
    //When and Then
    this.mockMvc.perform(delete(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Delete Success"));
  }

  //negative scenario
  // negative scenario
  @Test
  void testDeleteUserErrorWithNotFoundId() throws Exception {
    //Given
    //since service class return nothing
    doThrow(new ObjectNotFoundException("user", 5)).when(this.userService)
            .delete(5);

    //When and Then
    this.mockMvc.perform(MockMvcRequestBuilders.delete(this.baseUrl + "/users/5")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

}
//I will skip all controller case tests