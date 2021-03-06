package org.imster.cryptography;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/* Class that manages AES String-based password encryption of plaintext strings */
public class CryptoEncrypter extends CryptoResource {

    /* Initialises cryptographic resources */
    public CryptoEncrypter() throws CryptoException {
        super();
    }

    /* Encrypts plaintext string with password */
    public String encryptString(String plaintext, String password) throws IOException, CryptoException {

        if (plaintext.isEmpty())
            throw new IOException("Missing message to encrypt");

        if (password.isEmpty())
            throw new IOException("Missing password to encrypt");

        try {

            // Don't specify SHA1PRNG it is not an implementation requirement of the JVM
            // Allow implementation to use most preferred (secure) provider
            SecureRandom randomSecureRandom = new SecureRandom();

            // Generate salt
            final byte[] salt = new byte[SALT_SIZE];
            randomSecureRandom.nextBytes(salt);

            final char[] passwordCharArray = password.toCharArray();

            // Derive key given password and salt
            KeySpec spec = new PBEKeySpec(passwordCharArray, salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = secretKeyFactory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), KEY_SPEC_ALGORITHM);

            if (!USE_LEGACY_CBC) {

                final byte[] iv = new byte[GCM_INIT_VECTOR_SIZE];
                randomSecureRandom.nextBytes(iv);
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

                cipher.init(Cipher.ENCRYPT_MODE, secret, gcmParameterSpec);

                final byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

                // Copy salt and initialisation vector to front
                final byte[] encryptedWithSaltAndIV = new byte[encrypted.length + SALT_SIZE + GCM_INIT_VECTOR_SIZE];
                System.arraycopy(salt, 0, encryptedWithSaltAndIV, 0, SALT_SIZE);
                System.arraycopy(iv, 0, encryptedWithSaltAndIV, SALT_SIZE, GCM_INIT_VECTOR_SIZE);
                System.arraycopy(encrypted, 0, encryptedWithSaltAndIV, SALT_SIZE + GCM_INIT_VECTOR_SIZE, encrypted.length);

                return Base64.getEncoder().encodeToString(encryptedWithSaltAndIV);

            } else {

                // Initialise cipher and initialisation vector
                cipher.init(Cipher.ENCRYPT_MODE, secret);
                final byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();

                // Encrypt plaintext
                final byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

                // Copy salt and initialisation vector to front
                final byte[] encryptedWithSaltAndIV = new byte[encrypted.length + SALT_SIZE + INIT_VECTOR_SIZE];
                System.arraycopy(salt, 0, encryptedWithSaltAndIV, 0, SALT_SIZE);
                System.arraycopy(iv, 0, encryptedWithSaltAndIV, SALT_SIZE, INIT_VECTOR_SIZE);
                System.arraycopy(encrypted, 0, encryptedWithSaltAndIV, SALT_SIZE + INIT_VECTOR_SIZE, encrypted.length);

                // Return Base64 encoded ciphertext
                return Base64.getEncoder().encodeToString(encryptedWithSaltAndIV);

            }

        } catch (InvalidKeyException e) {
            CryptoException cryptoException = new CryptoException("Invalid key supplied");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (InvalidAlgorithmParameterException | InvalidParameterSpecException e) {
            CryptoException cryptoException = new CryptoException(
                    "Invalid cipher parameters supplied (Check initialisation vector");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (IllegalBlockSizeException e) {
            CryptoException cryptoException = new CryptoException("Plaintext has illegal block size");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (BadPaddingException e) {
            CryptoException cryptoException = new CryptoException("Plaintext has pad padding");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (InvalidKeySpecException e) {
            CryptoException cryptoException = new CryptoException("Invalid key specification supplied");
            cryptoException.initCause(e);
            throw cryptoException;
        }

    }

}
