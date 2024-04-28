package com.tjtechy.artifactsOnline.tjtechyuser;

import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UserServiceTest {
  @Mock
  UserRepository userRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  UserService userService;

  List<TJUser> users;

  @BeforeEach
  void setUp() {
    TJUser tjUser1 = new TJUser();
    tjUser1.setId(1);
    tjUser1.setUsername("tjbaba");
    tjUser1.setEnabled(true);
    tjUser1.setRoles("admin user");
    tjUser1.setPassword("123456");

    TJUser tjUser2 = new TJUser();
    tjUser2.setId(2);
    tjUser2.setUsername("idowu");
    tjUser2.setEnabled(true);
    tjUser2.setRoles("user");
    tjUser2.setPassword("78910");

    TJUser tjUser3 = new TJUser();
    tjUser3.setId(3);
    tjUser3.setUsername("tom");
    tjUser3.setEnabled(false);
    tjUser3.setRoles("user");
    tjUser3.setPassword("qwerty");

    //add user to list
    this.users = new ArrayList<>();
    this.users.add(tjUser1);
    this.users.add(tjUser2);
    this.users.add(tjUser3);

  }

  @AfterEach
  void tearDown() {
  }

  //1. find all users
  //positive scenario
  @Test
  void testFindAllSuccess() {
    //Given
    given(userRepository.findAll()).willReturn(this.users);

    //When
    List<TJUser> actualUsers = userService.findAll();

    //Then
    assertThat(actualUsers.size()).isEqualTo(this.users.size());
    verify(userRepository, times(1)).findAll();
  }

  //2. find a single user by Id
  //positive scenario
  @Test
  void testFindByIdSuccess(){
    TJUser user = new TJUser();
    user.setId(1);
    user.setUsername("tjbaba");
    user.setPassword("123456");
    user.setRoles("admin user");
    user.setEnabled(true);
    //Given
    //what I called from repo/DB mock
    given(userRepository.findById(1)).willReturn(Optional.of(user));

    //When
    //What I got
    TJUser returnedTJUser = userService.findById(1);

    //Then
    assertThat(returnedTJUser.getId()).isEqualTo(user.getId());
    assertThat(returnedTJUser.getUsername()).isEqualTo(user.getUsername());
    assertThat(returnedTJUser.getPassword()).isEqualTo(user.getPassword());
    assertThat(returnedTJUser.getRoles()).isEqualTo(user.getRoles());
    assertThat(returnedTJUser.isEnabled()).isEqualTo(user.isEnabled());

    verify(userRepository, times(1)).findById(1);
  }

  //3. find a single user by Id
  //negative scenario
  @Test
  void testFindByIdNotFound(){
    //Given
    given(userRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

    //When
    Throwable thrown = catchThrowable(()-> {
      TJUser returnedTJUser = userService.findById(1);
    });

    //Then
    assertThat(thrown)
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Could not find user with Id 1");
    verify(userRepository, times(1)).findById(1);
  }
  //4. create/save user to DB
  //POSITIVE scenario
  @Test
  void testSaveSuccess(){
    //Given
    TJUser newTJUser = new TJUser();
    newTJUser.setUsername("lily");
    newTJUser.setPassword("123456");
    newTJUser.setEnabled(true);
    newTJUser.setRoles("user");

    given(this.passwordEncoder.encode(newTJUser.getPassword())).willReturn("Encoded Password");
    given(this.userRepository.save(newTJUser)).willReturn(newTJUser);

    //When
    TJUser returnedUser = this.userService.save(newTJUser);

    //Then
    assertThat(returnedUser.getUsername()).isEqualTo(newTJUser.getUsername());
    assertThat(returnedUser.getPassword()).isEqualTo(newTJUser.getPassword());
    assertThat(returnedUser.isEnabled()).isEqualTo(newTJUser.isEnabled());
    assertThat(returnedUser.getRoles()).isEqualTo(newTJUser.getRoles());

    verify(this.userRepository, times(1)).save(newTJUser);
  }

  //5. update user
  //POSITIVE scenario
  @Test
  void testUpdateSuccess(){
    //Given
    //create some old user data that will be updated
    TJUser oldUser = new TJUser();
    oldUser.setId(1);
    oldUser.setUsername("john");
    oldUser.setPassword("123456");
    oldUser.setEnabled(true);
    oldUser.setRoles("admin user");

    TJUser update = new TJUser();
    update.setUsername("john-update");
    update.setPassword("123456");
    update.setEnabled(true);
    update.setRoles("admin user");

    given(this.userRepository.findById(1)).willReturn(Optional.of(oldUser));
    given(this.userRepository.save(oldUser)).willReturn(oldUser);

    //When
    TJUser updatedUser = this.userService.update(1, update);

    //Then
    assertThat(updatedUser.getId()).isEqualTo(1);
    assertThat(updatedUser.getUsername()).isEqualTo(update.getUsername());
    verify(this.userRepository, times(1)).findById(1);
    verify(this.userRepository, times(1)).save(oldUser);
  }

  // update user
  //negative scenario
  //complete this later
  @Test
  void testUpdateNotSuccess(){

  }

  //6. delete user
  //positive scenario
  @Test
  void testDeleteSuccess(){
    //Given
    //prepare a fake artifact to be deleted
    TJUser user = new TJUser();
    user.setId(1);
    user.setUsername("john");
    user.setPassword("123456");
    user.setEnabled(true);

    //mock the behaviours of repo findById
    //since the deleteById method in the repository does not return anything, we weill use doNothing() form mockito
    given(userRepository.findById(1)).willReturn(Optional.of(user));
    doNothing().when(userRepository).deleteById(1);

    //When
    userService.delete(1);

    //Then
    verify(this.userRepository, times(1)).deleteById(1);
  }

  //negative scenario
  @Test
  void testDeleteNotFound(){
    //Given
    given(userRepository.findById(1)).willReturn(Optional.empty());

    //When
    assertThrows(ObjectNotFoundException.class, () -> {
      userService.delete(1);
    });

    //Then
    verify(userRepository, times(1)).findById(1);
  }



}