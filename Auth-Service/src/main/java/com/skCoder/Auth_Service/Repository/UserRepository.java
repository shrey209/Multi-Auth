package com.skCoder.Auth_Service.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.skCoder.Auth_Service.Models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndProvider(String username, String provider); 
}
