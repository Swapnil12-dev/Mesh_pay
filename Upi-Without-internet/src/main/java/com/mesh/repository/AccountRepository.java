package com.mesh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mesh.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByVpa(String vpa);
}