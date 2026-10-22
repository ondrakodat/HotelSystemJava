package com.kodat.complexhotelsystem.services.jwt;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;


@Service
public class JwtService {
   private final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
   private final KeyPair keyPair;
   private final PrivateKey privateKey ;
   private final PublicKey publicKey ;
   private File publicKeyFile = new File("public.key");
   private File privateKeyFile = new File("private.key");

   public JwtService() throws NoSuchAlgorithmException {
       generator.initialize(2048);
       keyPair = generator.generateKeyPair();
       privateKey = keyPair.getPrivate();
       publicKey = keyPair.getPublic();
    }

    private void savePublicKey(){
       try (FileOutputStream fos = new FileOutputStream(publicKeyFile)){
          fos.write(publicKey.getEncoded());
       } catch (IOException e){
          System.out.println("Could not write public key message : " + e.getMessage());
       }
    }

    private void savePrivateKey(){
       try (FileOutputStream fos = new FileOutputStream(privateKeyFile)){
          fos.write(privateKey.getEncoded());
       } catch (IOException e){
          System.out.println("Could not write private key message : " + e.getMessage());
       }
    }

    public byte [] readPublicKey() throws IOException {
       return Files.readAllBytes(publicKeyFile.toPath());
    }

    public byte [] readPrivateKey() throws IOException {
      return Files.readAllBytes(privateKeyFile.toPath());
    }
}
