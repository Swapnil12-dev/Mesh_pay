package com.mesh.dto;

import java.math.BigDecimal;

public class PaymentInstruction {
	
	private String senderVpa;
	private String receiverVpa;
	private BigDecimal amount;
	private String nonce ;
	private Long signedAt;
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
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public Long getSignedAt() {
		return signedAt;
	}
	public void setSignedAt(Long signedAt) {
		this.signedAt = signedAt;
	}
	@Override
	public String toString() {
		return "PaymentInstruction [senderVpa=" + senderVpa + ", receiverVpa=" + receiverVpa + ", amount=" + amount
				+ ", nonce=" + nonce + ", signedAt=" + signedAt + "]";
	}
	public PaymentInstruction(String senderVpa, String receiverVpa, BigDecimal amount, String nonce, Long signedAt) {
		super();
		this.senderVpa = senderVpa;
		this.receiverVpa = receiverVpa;
		this.amount = amount;
		this.nonce = nonce;
		this.signedAt = signedAt;
	}
	public PaymentInstruction() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
