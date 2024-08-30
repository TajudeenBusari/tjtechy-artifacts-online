package com.tjtechy.artifactsOnline.tjtechyuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for User API endpoints")
@Tag("Integration")
@ActiveProfiles(value="development")

class TJUserControllerIntegrationTest {
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  String token;
  @Value("/api/v1")
  String baseUrl;

  @BeforeEach
  void SetUp() throws Exception{
    var resultAction = this.mockMvc.perform(post(this.baseUrl + "/users/login")
            .with(httpBasic("john", "123456")));
    MvcResult mvcResult = resultAction.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();
    var json = new JSONObject(contentAsString);
    this.token = "Bearer " + json.getJSONObject("data").getString("token");
  }

  @Test
  @DisplayName("Check findAllUsers (GET)")
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)

  void testFindAllUsersSuccess() throws Exception {
    this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
  }

  @Test
  @DisplayName("Check findUserById (GET): User with ROLE_admin accessing Any User's Info")
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void testFindUsersByIdWithAdminAccessAnyUsersInfoSuccess() throws Exception {
    this.mockMvc.perform(get(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value(2))
            .andExpect(jsonPath("$.data.username").value("eric"));
  }

  @Test
  @DisplayName("Check findUserById (GET): User with ROLE_user Accessing Own Info")

  void testFindUsersByIdWithUserAccessingOwnInfoSuccess() throws Exception {

    var resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("eric", "654321")));
    MvcResult mvcResult = resultActions.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();
    JSONObject json = new JSONObject(contentAsString);
    String ericToken = "Bearer " + json.getJSONObject("data").getString("token");

    this.mockMvc.perform(get(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, ericToken))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value(2))
            .andExpect(jsonPath("$.data.username").value("eric"));
  }

  @Test
  @DisplayName("Check findUserById (GET): User with ROLE_user Accessing Another User's Info")

  //negative scenario
  void testFindUsersByIdWithUserAccessingAnotherUsersInfoSuccess() throws Exception {

    var resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("eric", "654321")));
    MvcResult mvcResult = resultActions.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();
    JSONObject json = new JSONObject(contentAsString);
    String ericToken = "Bearer " + json.getJSONObject("data").getString("token");

    //eric is accessing john's info
    //it should not be possible
    this.mockMvc.perform(get(this.baseUrl + "/users/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, ericToken))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
            .andExpect(jsonPath("$.message").value("No permission"))
            .andExpect(jsonPath("$.data").value("Access Denied"));

  }

  @Test
  @DisplayName("Check findUserById with non-existing id (GET)")
  void testFindUsersByIdNotFound() throws Exception {
    this.mockMvc.perform(get(this.baseUrl + "/users/5").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Check deleteUser with insufficient permission id (DELETE)")
  void testDeleteUserNoAccessAsRoleUser() throws Exception {

    var resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("eric", "654321")));
    MvcResult mvcResult = resultActions.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();
    JSONObject json = new JSONObject(contentAsString);
    String ericToken = "Bearer " + json.getJSONObject("data").getString("token");

    this.mockMvc.perform(delete(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, ericToken))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
            .andExpect(jsonPath("$.message").value("No permission"))
            .andExpect(jsonPath("$.data").value("Access Denied"));

    this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(3)))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].username").value("john"));
  }

  @Test
  @DisplayName("Add User with valid input (POST)")
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void testAddUserSuccess() throws Exception{
    var tjUser = new TJUser();
    tjUser.setUsername("lily");
    tjUser.setPassword("123456");
    tjUser.setEnabled(true);
    tjUser.setRoles("admin user"); //The delimiter is space


    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(post(this.baseUrl + "/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Add Success"))
            .andExpect(jsonPath("$.data.id").isNotEmpty())
            .andExpect(jsonPath("$.data.username").value("lily"))
            .andExpect(jsonPath("$.data.enabled").value(true))
            .andExpect(jsonPath("$.data.roles").value("admin user"));
    this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
  }


  @Test
  @DisplayName("Add User with invalid input (POST)")
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    //negative scenario
  void testAddUserWithInvalidInputSuccess() throws Exception{
    var tjUser = new TJUser();

    tjUser.setUsername("");
    tjUser.setPassword("");
    tjUser.setRoles("");


    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(post(this.baseUrl + "/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
            .andExpect(jsonPath("$.message").value("provided argument are invalid, see data for details."))
            .andExpect(jsonPath("$.data.username").value("username is required"))
            .andExpect(jsonPath("$.data.password").value("password is required"))
            .andExpect(jsonPath("$.data.roles").value("roles are required"));
    this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
  }

  @Test
  @DisplayName("Update User with valid input (PUT)")
  //john updating tom's info. it should be possible, he is an admin
  void testUpdateUserWithAdminUpdatingAnyUserInfoSuccess() throws Exception{
    var tjUser = new TJUser();
    tjUser.setUsername("tom123"); //it was tom
    tjUser.setEnabled(false);
    tjUser.setRoles("user");

    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(put(this.baseUrl + "/users/3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Update Success"))
            .andExpect(jsonPath("$.data.id").value(3))
            .andExpect(jsonPath("$.data.username").value("tom123"))
            .andExpect(jsonPath("$.data.enabled").value(false))
            .andExpect(jsonPath("$.data.roles").value("user"));
  }

  @Test
  @DisplayName("Update User with invalid id (PUT)")
    //john updating own info with invalid id.
  void testUpdateUserWithNonExistentIdSuccess() throws Exception{
    var tjUser = new TJUser();
    tjUser.setId(5);
    tjUser.setUsername("john123"); //it was tom
    tjUser.setEnabled(true);
    tjUser.setRoles("admin user");

    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(put(this.baseUrl + "/users/5")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Update User with invalid input (PUT)")
    //john own info with invalid data
  void testUpdateUserWithInvalidInputSuccess() throws Exception{
    var tjUser = new TJUser();
    tjUser.setId(1);
    tjUser.setUsername(""); //it was tom
    tjUser.setRoles("");

    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(put(this.baseUrl + "/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
            .andExpect(jsonPath("$.message").value("provided argument are invalid, see data for details."))
            .andExpect(jsonPath("$.data.username").value("username is required"))
            .andExpect(jsonPath("$.data.roles").value("roles are required"));

    this.mockMvc.perform(get(this.baseUrl + "/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.username").value("john"));
  }

  @Test
  @DisplayName("Update User with valid input (PUT): User with ROLE_user Updating Own Info")
  void testUpdateUserWithUserUpdatingOwnInfoSuccess() throws Exception{

    var resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("eric", "654321")));
    MvcResult mvcResult = resultActions.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();
    JSONObject json = new JSONObject(contentAsString);
    String ericToken = "Bearer " + json.getJSONObject("data").getString("token");

    var tjUser = new TJUser();
    tjUser.setUsername("eric123"); //it was tom
    tjUser.setEnabled(true);
    tjUser.setRoles("user");

    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(put(this.baseUrl + "/users/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, ericToken))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Update Success"))
            .andExpect(jsonPath("$.data.id").value(2))
            .andExpect(jsonPath("$.data.username").value("eric123"))
            .andExpect(jsonPath("$.data.enabled").value(true))
            .andExpect(jsonPath("$.data.roles").value("user"));
  }

  @Test
  @DisplayName("Update User with valid input (PUT): User with ROLE_user Updating Another User Info")
    //negative scenario
    //eric updating tom's info, it should not be possible

  void testUpdateUserWithUserUpdatingAnotherUserInfoSuccess() throws Exception{

    var resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("eric", "654321")));
    MvcResult mvcResult = resultActions.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();
    JSONObject json = new JSONObject(contentAsString);
    String ericToken = "Bearer " + json.getJSONObject("data").getString("token");

    var tjUser = new TJUser();
    tjUser.setUsername("tom123"); //it was tom
    tjUser.setEnabled(false);
    tjUser.setRoles("user");

    var jsonString = this.objectMapper.writeValueAsString(tjUser);

    this.mockMvc.perform(put(this.baseUrl + "/users/3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, ericToken))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
            .andExpect(jsonPath("$.message").value("No permission"))
            .andExpect(jsonPath("$.data").value("Access Denied"));
  }

  @Test
  @DisplayName("Delete User with valid input (DELETE)")
  void testDeleteUserSuccess() throws Exception{

    this.mockMvc.perform(delete(this.baseUrl + "/users/2")
                    .accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Delete Success"))
            .andExpect(jsonPath("$.data").isEmpty());

    this.mockMvc.perform(get(this.baseUrl + "/users/2")
                    .accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find user with Id 2"))
            .andExpect(jsonPath("$.data").isEmpty());
  }


}
