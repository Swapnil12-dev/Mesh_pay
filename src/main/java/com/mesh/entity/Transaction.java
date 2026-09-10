package com.mesh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
		name = "transactions",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_transaction_nonce",
						columnNames = "nonce"
				)
		}
)
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

	@Column(nullable = false, unique = true)
	private String nonce;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	public enum Status {
		SETTLED,
		REJECTED
	}
}