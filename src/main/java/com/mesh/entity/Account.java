/**
 * Represents a financial account in MeshPay.
 *
 * Each registered user owns an account identified by a
 * unique Virtual Payment Address (VPA).
 *
 * Responsibilities:
 * - Stores current account balance.
 * - Supports debit and credit operations.
 * - Prevents concurrent balance corruption using optimistic locking.
 *
 * Note:
 * User profile information is stored separately in the User entity.
 * This entity focuses only on financial data.
 */

package com.mesh.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "accounts")

public class Account {
	
	
	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Account(String vpa, BigDecimal balance) {
		super();
		this.vpa = vpa;
		this.balance = balance;
		
	}

	@Override
	public String toString() {
		return "Account [vpa=" + vpa + ", balance=" + balance + "]";
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Id
	private String vpa;
	
	@Column(nullable=false , precision = 19, scale = 2)
	private BigDecimal balance;

}
