package com.mesh.repository;

import com.mesh.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
		extends JpaRepository<Transaction, Long> {

	List<Transaction> findBySenderVpa(String senderVpa);

	Optional<Transaction> findByNonce(String nonce);
}