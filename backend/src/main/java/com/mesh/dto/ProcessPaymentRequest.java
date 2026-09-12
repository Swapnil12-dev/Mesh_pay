package com.mesh.dto;

public class ProcessPaymentRequest {

    private String ciphertext;

    public ProcessPaymentRequest() {
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
}