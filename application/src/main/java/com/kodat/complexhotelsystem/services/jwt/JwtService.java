package com.kodat.complexhotelsystem.services.jwt;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Service
public class JwtService {
   private final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
   private KeyPair keyPair;
   private  PrivateKey privateKey ;
   private  PublicKey publicKey ;
   private File publicKeyFile = new File("public.key");
   private File privateKeyFile = new File("private.key");

   public JwtService() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
       initializeKeys();
    }

    private void savePublicKey(){
       try (FileOutputStream fos = new FileOutputStream(publicKeyFile)){
          fos.write(publicKey.getEncoded());
       } catch (IOException e){
           publicKeyFile.delete();
           System.out.println("Could not write public key to file, message : " + e.getMessage());
       }
    }

    private void savePrivateKey(){
       try (FileOutputStream fos = new FileOutputStream(privateKeyFile)){
          fos.write(privateKey.getEncoded());
       } catch (IOException e){
           privateKeyFile.delete();
           System.out.println("Could not write private key to file, message : " + e.getMessage());
       }
    }

    public byte [] readPublicKey() throws IOException {
       return Files.readAllBytes(publicKeyFile.toPath());
    }

    public byte [] readPrivateKey() throws IOException {
      return Files.readAllBytes(privateKeyFile.toPath());
    }

    public PublicKey recreateActualInstanceOfPublicKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
       EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(readPublicKey());
       return keyFactory.generatePublic(publicKeySpec);
    }

    public PrivateKey recreateActualInstanceOfPrivateKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
       EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(readPrivateKey());
       return keyFactory.generatePrivate(privateKeySpec);
    }

    public void initializeKeys() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(publicKeyFile.exists() && privateKeyFile.exists()){
            publicKey = recreateActualInstanceOfPublicKey();
            privateKey = recreateActualInstanceOfPrivateKey();
        }else{
            generator.initialize(2048);
            keyPair = generator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            savePrivateKey();
            savePublicKey();
        }
    }

}

