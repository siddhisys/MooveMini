package com.moove.util;

import java.util.logging.Logger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
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

    private static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // Derive AES key using PBKDF2 and username
    private static SecretKey getAESKeyFromUsername(char[] username, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(username, salt, 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return secret;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.log(Level.SEVERE, "Error creating AES key", ex);
            return null;
        }
    }

    // Encrypt password using username
    public static String encrypt(String username, String password) {
        try {
            // Log the encryption process without revealing the password
            logger.info("Encrypting password for user: " + username);
            
            byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
            byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

            SecretKey aesKey = getAESKeyFromUsername(username.toCharArray(), salt);
            
            if (aesKey == null) {
                logger.severe("Failed to generate AES key");
                return null;
            }

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] cipherText = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // Combine IV + salt + cipherText
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
            ex.printStackTrace();
            return null;
        }
    }

    // Decrypt password using username
    public static String decrypt(String encryptedPassword, String username) {
        try {
            logger.info("Decrypting password for user: " + username);
            
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);

            ByteBuffer bb = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);

            byte[] salt = new byte[SALT_LENGTH_BYTE];
            bb.get(salt);

            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            SecretKey aesKey = getAESKeyFromUsername(username.toCharArray(), salt);
            
            if (aesKey == null) {
                logger.severe("Failed to regenerate AES key for decryption");
                return null;
            }

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] plainText = cipher.doFinal(cipherText);
            logger.info("Password decryption successful");
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Decryption failed", ex);
            ex.printStackTrace();
            return null;
        }
    }
}