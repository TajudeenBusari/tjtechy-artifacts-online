package com.tjtechy.artifactsOnline.tjtechyuser;

import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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
    //we need to encode plain text password before saving to the DB-->TODO
    newTJUser.setPassword(this.passwordEncoder.encode(newTJUser.getPassword()));
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

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //loadUserByUsername returns UserDetails and not TJUser
    //we use Adapter Pattern to convert TJUser to UserDetails
    //this is what the authentication provider expects (userDetails)
    //if found, then map to userDetails in the MyUserPrincipal else throw an exception

    return this.userRepository.findByUsername(username)
            .map(tjUser -> new MyUserPrincipal(tjUser))
            .orElseThrow(()->new UsernameNotFoundException("username " + username + " is not found"));
  }
}


/*Inject the PasswordEncoder into this class to encode password
*we get the password from newTJUser, encode it, using PasswordEncoder and set it
* as password. We can then save in the DB via userRepository as encrypted
* In the DBInitializer class, instead of using the userRepository, we use the
* userService
* Handle the UsernameNotFoundException in the ExceptionHandlerAdvice class
*
*
* */