/**
 * Represents a payment processed by MeshPay.
 *
 * Every payment reaching the backend is recorded as a
 * transaction for auditing and tracking purposes.
 *
 * Responsibilities:
 * - Stores sender and receiver details.
 * - Stores payment amount.
 * - Stores transaction execution status.
 *
 * Note:
 * Transactions are immutable records and should never be modified
 * after creation.
 */

package com.mesh.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderVpa;

    @Column(nullable = false)
    private String receiverVpa;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        SETTLED,
        REJECTED
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSenderVpa() {
		return senderVpa;
	}

	public void setSenderVpa(String senderVpa) {
		this.senderVpa = senderVpa;
	}

	public String getReceiverVpa() {
		return receiverVpa;
	}

	public void setReceiverVpa(String receiverVpa) {
		this.receiverVpa = receiverVpa;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", senderVpa=" + senderVpa + ", receiverVpa=" + receiverVpa + ", amount="
				+ amount + ", status=" + status + "]";
	}

	public Transaction(Long id, String senderVpa, String receiverVpa, BigDecimal amount, Status status) {
		super();
		this.id = id;
		this.senderVpa = senderVpa;
		this.receiverVpa = receiverVpa;
		this.amount = amount;
		this.status = status;
	}

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}