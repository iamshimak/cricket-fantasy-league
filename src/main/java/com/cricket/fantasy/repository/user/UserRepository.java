package com.cricket.fantasy.repository.user;

import com.cricket.fantasy.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> findByUsernameIn(Collection<String> usernames);
}