package com.weg.quicktransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.quicktransfer.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}
