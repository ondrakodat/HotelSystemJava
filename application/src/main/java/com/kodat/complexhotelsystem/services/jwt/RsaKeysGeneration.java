package com.kodat.complexhotelsystem.services.jwt;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Path;


@Service
@Getter
public class RsaKeysGeneration {
   private  PrivateKey privateKey ;
   private  PublicKey publicKey ;
    @Value("${app.jwt.public-key-path}")
    private String publicKeyPath;

    @Value("${app.jwt.private-key-path}")
    private String privateKeyPath;

    public RsaKeysGeneration(
            @Value("${app.jwt.private-key-path}") String privateKeyPath,
            @Value("${app.jwt.public-key-path}") String publicKeyPath
    ) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        this.privateKeyPath = privateKeyPath;
        this.publicKeyPath = publicKeyPath;

        initializeKeys();
    }

    public byte [] readPublicKey() throws IOException {
        return Files.readAllBytes(Paths.get(publicKeyPath));
    }

    public byte [] readPrivateKey() throws IOException {
        return Files.readAllBytes(Paths.get(privateKeyPath));
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
        if (!Files.exists(Paths.get(privateKeyPath))
                || !Files.exists(Paths.get(publicKeyPath))) {

            throw new IllegalStateException("RSA keys not found!");
        }

        publicKey = recreateActualInstanceOfPublicKey();
        privateKey = recreateActualInstanceOfPrivateKey();
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);

        return generator.generateKeyPair();
    }

    public static void saveKeyPair(KeyPair keyPair, String privateKeyPath, String publicKeyPath)
            throws IOException {

        Path privatePath = Paths.get(privateKeyPath);
        Path publicPath = Paths.get(publicKeyPath);

        Files.createDirectories(privatePath.getParent());
        Files.createDirectories(publicPath.getParent());

        if (Files.exists(privatePath) || Files.exists(publicPath)) {
            throw new IllegalStateException(
                    "RSA key file already exists. Generation cancelled."
            );
        }

        Files.write(privatePath, keyPair.getPrivate().getEncoded(),
                java.nio.file.StandardOpenOption.CREATE_NEW);

        Files.write(publicPath, keyPair.getPublic().getEncoded(),
                java.nio.file.StandardOpenOption.CREATE_NEW);

    }

    public static void main(String[] args) throws Exception {

        KeyPair keyPair = generateKeyPair();

        saveKeyPair(
                keyPair,
                "C:/Users/revel/SecretHotel/private.key",
                "C:/Users/revel/SecretHotel/public.key"
        );

        System.out.println("RSA keys generated successfully!");
    }

}


