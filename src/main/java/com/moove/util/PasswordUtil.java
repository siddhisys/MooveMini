package com.moove.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordUtil {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Logger logger = Logger.getLogger(PasswordUtil.class.getName());
    private static final String KEY_DERIVATION_PASSWORD = "MooveStaticKey"; // Replace with a secure, static key

    private static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private static SecretKey getAESKey(byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(KEY_DERIVATION_PASSWORD.toCharArray(), salt, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.log(Level.SEVERE, "Error creating AES key", ex);
            throw new RuntimeException("Failed to generate AES key", ex);
        }
    }

    public static String encrypt(String password) {
        try {
            logger.info("Encrypting password");
            byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
            byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
            SecretKey aesKey = getAESKey(salt);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] cipherText = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedData = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherText)
                    .array();

            String result = Base64.getEncoder().encodeToString(encryptedData);
            logger.info("Password encryption successful");
            return result;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Encryption failed", ex);
            throw new RuntimeException("Password encryption failed", ex);
        }
    }

    public static String decrypt(String encryptedPassword) {
        try {
            logger.info("Decrypting password");
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            ByteBuffer bb = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);
            byte[] salt = new byte[SALT_LENGTH_BYTE];
            bb.get(salt);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            SecretKey aesKey = getAESKey(salt);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] plainText = cipher.doFinal(cipherText);
            logger.info("Password decryption successful");
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Decryption failed", ex);
            throw new RuntimeException("Password decryption failed", ex);
        }
    }
}