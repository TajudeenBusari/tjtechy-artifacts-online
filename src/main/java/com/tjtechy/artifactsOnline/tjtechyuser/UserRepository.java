package com.tjtechy.artifactsOnline.tjtechyuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<TJUser, Integer> {

  Optional<TJUser>findByUsername(String username);

}

/*
//1. Since it is an interface, you don't need to provide any
implementation.
2. Just like findById, the return type should be optional
since it is possible, username may not be found
3. The spring data jpa will provide the implementation for the
findByUsername(even though it is custom method),
since we are following the same naming convention find by something
4. For example, you can also define the following if needed here:
 List<TJUser>findByEnabled(boolean enabled);
 Optional<TJUser>findByUsernameAndPassword(String username, String password)
5. Read the doc on Derived Query Method
 */