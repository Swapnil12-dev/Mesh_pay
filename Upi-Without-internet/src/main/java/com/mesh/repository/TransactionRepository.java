package com.mesh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mesh.entity.Transaction;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
	
	Optional<Transaction> findBySenderVpa(String senderVpa);


}