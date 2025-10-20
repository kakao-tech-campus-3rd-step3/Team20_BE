package com.example.kspot.users.repository;

import com.example.kspot.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findUsersByEmail(String email);
    Optional<Users> findUsersByNickname(String nickname);
}
