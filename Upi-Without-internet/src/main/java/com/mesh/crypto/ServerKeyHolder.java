package com.mesh.crypto;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Holds the backend RSA key pair.
 *
 * Public Key:
 *  - Shared with sender devices.
 *  - Used to encrypt AES keys.
 *
 * Private Key:
 *  - Never leaves the backend.
 *  - Used to decrypt AES keys.
 *
 * In production these keys would be stored securely.
 * For the demo we generate them at application startup.
 */
@Component
public class ServerKeyHolder {

    private static KeyPair keyPair;

    public ServerKeyHolder() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            this.keyPair = generator.generateKeyPair();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

    public static PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}