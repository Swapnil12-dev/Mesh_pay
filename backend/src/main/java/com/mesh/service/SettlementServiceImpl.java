package com.mesh.service;

import com.mesh.crypto.HybridCryptoService;
import com.mesh.dto.PaymentInstruction;
import com.mesh.entity.Account;
import com.mesh.entity.Transaction;
import com.mesh.repository.AccountRepository;
import com.mesh.repository.TransactionRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class SettlementServiceImpl
        implements SettlementService {

    private final HybridCryptoService hybridCryptoService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public SettlementServiceImpl(
            HybridCryptoService hybridCryptoService,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.hybridCryptoService =
                hybridCryptoService;

        this.accountRepository =
                accountRepository;

        this.transactionRepository =
                transactionRepository;
    }

    @Override
    @Transactional
    public void processPayment(
            String ciphertext) throws Exception {

        // ============================================
        // 1. DECRYPT PAYMENT
        // ============================================

        PaymentInstruction instruction =
                hybridCryptoService.decrypt(
                        ciphertext
                );


        // ============================================
        // 2. CHECK REPLAY / DUPLICATE
        // ============================================

        if (
                transactionRepository
                        .findByNonce(
                                instruction.getNonce()
                        )
                        .isPresent()
        ) {

            throw new IllegalStateException(
                    "Duplicate payment rejected. " +
                            "Transaction has already been processed."
            );
        }


        // ============================================
        // 3. FIND SENDER
        // ============================================

        Account sender =
                accountRepository
                        .findByVpa(
                                instruction.getSenderVpa()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Sender account not found"
                                )
                        );


        // ============================================
        // 4. FIND RECEIVER
        // ============================================

        Account receiver =
                accountRepository
                        .findByVpa(
                                instruction.getReceiverVpa()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Receiver account not found"
                                )
                        );


        // ============================================
        // 5. CHECK BALANCE
        // ============================================

        if (
                sender.getBalance()
                        .compareTo(
                                instruction.getAmount()
                        ) < 0
        ) {

            throw new IllegalStateException(
                    "Insufficient balance"
            );
        }


        // ============================================
        // 6. DEBIT SENDER
        // ============================================

        sender.setBalance(
                sender.getBalance()
                        .subtract(
                                instruction.getAmount()
                        )
        );


        // ============================================
        // 7. CREDIT RECEIVER
        // ============================================

        receiver.setBalance(
                receiver.getBalance()
                        .add(
                                instruction.getAmount()
                        )
        );


        // ============================================
        // 8. SAVE ACCOUNTS
        // ============================================

        accountRepository.save(sender);

        accountRepository.save(receiver);


        // ============================================
        // 9. SAVE TRANSACTION
        // ============================================

        Transaction transaction =
                Transaction.builder()

                        .senderVpa(
                                instruction.getSenderVpa()
                        )

                        .receiverVpa(
                                instruction.getReceiverVpa()
                        )

                        .amount(
                                instruction.getAmount()
                        )

                        .nonce(
                                instruction.getNonce()
                        )

                        .status(
                                Transaction.Status.SETTLED
                        )

                        .build();


        transactionRepository.save(
                transaction
        );
    }
}