package com.tjtechy.artifactsOnline.tjtechyuser;

import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  //find all Users
  public List<TJUser> findAll(){

    return this.userRepository.findAll();
  }

  //find a user
  public TJUser findById(Integer userId){

    return this.userRepository.findById(userId)
            .orElseThrow(()-> new ObjectNotFoundException("user", userId));
  }

  //create a user
  public TJUser save(TJUser newTJUser){
    //we need to encode plain password before saving to the DB-->TODO
    return this.userRepository.save(newTJUser);
  }

  //update user
  public TJUser update(Integer userId, TJUser update){
    //first find by id or else throw exception
    TJUser oldTJUser = this.userRepository.findById(userId)
            .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    oldTJUser.setUsername(update.getUsername());
    oldTJUser.setEnabled(update.isEnabled());
    oldTJUser.setRoles(update.getRoles());
    return this.userRepository.save(oldTJUser);
  }

  //delete user
  public void delete(Integer userId){
    this.userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", userId));
    this.userRepository.deleteById(userId);
  }

}
