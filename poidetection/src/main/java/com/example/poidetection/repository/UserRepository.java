package com.example.poidetection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.poidetection.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}