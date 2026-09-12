package com.mesh.controller;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mesh.crypto.ServerKeyHolder;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    private final ServerKeyHolder serverKeyHolder;

    public CryptoController(
            ServerKeyHolder serverKeyHolder) {

        this.serverKeyHolder = serverKeyHolder;
    }

    @GetMapping("/public-key")
    public Map<String, String> getPublicKey() {

        PublicKey publicKey =
                serverKeyHolder.getPublicKey();

        return Map.of(
                "algorithm", "RSA",
                "format", publicKey.getFormat(),
                "publicKey",
                Base64.getEncoder()
                        .encodeToString(
                                publicKey.getEncoded()
                        )
        );
    }
}