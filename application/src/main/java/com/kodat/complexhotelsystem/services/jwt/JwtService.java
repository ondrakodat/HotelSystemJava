package com.kodat.complexhotelsystem.services.jwt;

import org.springframework.stereotype.Service;

import java.security.*;


@Service
public class JwtService {
   private KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");;
   private KeyPair keyPair;
   private PrivateKey privateKey ;
   private PublicKey publicKey ;


    public JwtService() throws NoSuchAlgorithmException {
       generator.initialize(2048);
       keyPair = generator.generateKeyPair();
       privateKey = keyPair.getPrivate();
       publicKey = keyPair.getPublic();
    }
}
