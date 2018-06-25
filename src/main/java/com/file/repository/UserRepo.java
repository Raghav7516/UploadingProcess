package com.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.file.model.User;

public interface UserRepo extends JpaRepository<User, Integer> {

}
