package com.mesh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mesh.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByPhone(String Phone);
	Optional<User> findByVpa(String vpa);
	

}
