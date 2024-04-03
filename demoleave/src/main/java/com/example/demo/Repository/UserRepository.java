package com.example.demo.Repository;

import com.example.demo.Entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByIdAndPassword(Integer id, String password);
    List<User> findByJobTitleNot(String jobTitle);
	Optional<User> findByName(String userName);
}
