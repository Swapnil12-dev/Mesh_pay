package com.mesh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mesh.dto.CreatePaymentRequest;
import com.mesh.dto.MeshPacket;
import com.mesh.dto.ProcessPaymentRequest;
import com.mesh.service.MeshPacketService;
import com.mesh.service.SettlementService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private MeshPacketService meshPacketService;
    
    @Autowired
    private SettlementService settlementService;

    @PostMapping("/create")
    public MeshPacket createPayment(
            @RequestBody CreatePaymentRequest request) throws Exception {

        return meshPacketService.createPacket(
                request.getSenderVpa(),
                request.getReceiverVpa(),
                request.getAmount()
        );
    }
    
    @PostMapping("/process")
    public String processPayment(
            @RequestBody ProcessPaymentRequest request)
            throws Exception {

        settlementService.processPayment(
                request.getCiphertext());

        return "Payment Processed Successfully";
    }
}