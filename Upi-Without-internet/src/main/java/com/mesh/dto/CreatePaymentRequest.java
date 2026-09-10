package com.mesh.dto;

import java.math.BigDecimal;

public class CreatePaymentRequest {

    private String senderVpa;
    private String receiverVpa;
    private BigDecimal amount;

    public CreatePaymentRequest() {
    }

    public CreatePaymentRequest(String senderVpa, String receiverVpa, BigDecimal amount) {
        this.senderVpa = senderVpa;
        this.receiverVpa = receiverVpa;
        this.amount = amount;
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
}