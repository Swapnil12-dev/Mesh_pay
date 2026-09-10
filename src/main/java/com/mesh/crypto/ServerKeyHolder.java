package com.mesh.crypto;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class ServerKeyHolder {

    private final KeyPair keyPair;

    public ServerKeyHolder() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);

            this.keyPair = generator.generateKeyPair();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate RSA key pair", e
            );
        }
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}