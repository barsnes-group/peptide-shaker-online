package com.uib.web.peptideshaker.model.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * This class is utility for sharing links cross Web PeptideShaker users
 *
 * @author Yehia Farag
 */
public class LinkUtil {

    private final Random rand;
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;
    private final BASE64Decoder decoder = new BASE64Decoder();
    private final BASE64Encoder encoder = new BASE64Encoder();

    /**
     * Initialise the utility
     */
    public LinkUtil() {
        this.rand = new Random((new Date()).getTime());
        try {
            myEncryptionKey = "ThisIsSpartaThisIsSparta";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            try {
                skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LinkUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            cipher = Cipher.getInstance(myEncryptionScheme);
            try {
                key = skf.generateSecret(ks);
            } catch (InvalidKeySpecException ex) {
                ex.printStackTrace();
            }
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Encrypt the shared link
     *
     * @param linkToShare the link to be encrypted
     * @return encrypted link
     */
    public String encrypt(String linkToShare) {

        linkToShare = inencrypt(linkToShare);

        byte[] salt = new byte[8];

//        rand.nextBytes(salt);

        return encoder.encode(salt) + encoder.encode(linkToShare.getBytes());
    }

    /**
     * Decrypt the shared link
     *
     * @param encstr encrypted link
     * @return decrypted link
     */
    public String decrypt(String encstr) {

        if (encstr.length() > 12) {

            String cipher = encstr.substring(12);

            try {

                return indecrypt(new String(decoder.decodeBuffer(cipher)));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    /**
     * Prepare link to be encrypted
     *
     * @param unencryptedString link to be encrypted
     * @return encrypted link
     */
    public String inencrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.getEncoder().encode(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    /**
     * initialise link to be decyoted
     *
     * @param encryptedString encrypted link
     * @return decrypted link
     */
    public String indecrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
}
