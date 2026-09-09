package com.mesh.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mesh.crypto.HybridCryptoService;
import com.mesh.crypto.ServerKeyHolder;
import com.mesh.dto.MeshPacket;
import com.mesh.dto.PaymentInstruction;


@Service
public class MeshPacketServiceImpl implements MeshPacketService {

	@Override
	public MeshPacket createPacket(String senderVpa, String receiverVpa, BigDecimal amount) throws Exception {
		
	    // Create payment instruction
        PaymentInstruction instruction = new PaymentInstruction();
        HybridCryptoService hybridCryptoService = new HybridCryptoService();
        ServerKeyHolder serverKeyHolder = new ServerKeyHolder();

        instruction.setSenderVpa(senderVpa);
        instruction.setReceiverVpa(receiverVpa);
        instruction.setAmount(amount);
        instruction.setNonce(UUID.randomUUID().toString());
        instruction.setSignedAt(System.currentTimeMillis());
        
        // Encrypt payment
        String ciphertext = hybridCryptoService.encrypt(
                instruction,
                serverKeyHolder.getPublicKey()
        );
        
        
        // Create mesh packet
        MeshPacket packet = new MeshPacket();

        packet.setPacketId(UUID.randomUUID().toString());
        packet.setTtl(5);
        packet.setCreatedAt(System.currentTimeMillis());
        packet.setCiphertext(ciphertext);

        return packet;
	
}
}
