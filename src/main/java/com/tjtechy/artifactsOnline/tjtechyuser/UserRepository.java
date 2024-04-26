package com.tjtechy.artifactsOnline.tjtechyuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<TJUser, Integer> {
}
