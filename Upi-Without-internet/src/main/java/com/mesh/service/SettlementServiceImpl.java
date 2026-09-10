package com.mesh.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mesh.crypto.HybridCryptoService;
import com.mesh.dto.PaymentInstruction;
import com.mesh.entity.Account;
import com.mesh.entity.Transaction;
import com.mesh.repository.AccountRepository;
import com.mesh.repository.TransactionRepository;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private HybridCryptoService hybridCryptoService;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void processPayment(String ciphertext) throws Exception {

        // Decrypt packet
        PaymentInstruction instruction =
                hybridCryptoService.decrypt(ciphertext);
  

        Account sender =
                accountRepository.findById(
                        instruction.getSenderVpa())
                        .orElseThrow();

        Account receiver =
                accountRepository.findById(
                        instruction.getReceiverVpa())
                        .orElseThrow();

        BigDecimal amount = instruction.getAmount();
        
        // Balance Check
        if(sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient Balance");
        }


        // Debit Sender
        sender.setBalance(
                sender.getBalance().subtract(amount));

        // Credit Receiver
        receiver.setBalance(
                receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);
        
        Transaction tx = new Transaction();


        tx.setSenderVpa(instruction.getSenderVpa());
        tx.setReceiverVpa(instruction.getReceiverVpa());
        tx.setAmount(amount);
        tx.setStatus(Transaction.Status.SETTLED);

        transactionRepository.save(tx);
    }
}