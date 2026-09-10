package com.mesh.service;

import java.math.BigDecimal;

import com.mesh.dto.MeshPacket;

public interface MeshPacketService {

    MeshPacket createPacket(
            String senderVpa,
            String receiverVpa,
            BigDecimal amount
    ) throws Exception;
}
