package com.eagb.blockchainexample.utils;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class CipherUtils {

    private static final String PASSWORD = "Th15-15-4-P455w0rd";
    private static final String ALGORITHM = "DES";

    // This method is used to encrypt the message, but this feature is not necessary in Blockchain,
    // so I just added this to keep the example more interesting
    public static String encryptIt(@NonNull String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(PASSWORD.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes(StandardCharsets.UTF_8);
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);

        } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException |
                BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return value;
    }

    // This method is not used in this app, but it's not bad to know how to decrypt the text
    public static String decryptIt(@NonNull String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(PASSWORD.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedValueBytes = (cipher.doFinal(encryptedPwdBytes));

            return new String(decryptedValueBytes);

        } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException |
                BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return value;
    }
}
