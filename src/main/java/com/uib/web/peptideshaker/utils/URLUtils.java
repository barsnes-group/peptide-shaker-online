package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.model.CONSTANT;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class URLUtils {

    private final Random rand;
    byte[] arrayBytes;
    SecretKey key;
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    private String myEncryptionKey;
    private String myEncryptionScheme;

    public URLUtils() {
        this.rand = new Random((new Date()).getTime());
        try {
            myEncryptionKey = "ThisIsSpartaThisIsSparta";
            myEncryptionScheme = CONSTANT.DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(CONSTANT.UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            try {
                skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            } catch (NoSuchAlgorithmException ex) {
            }
            cipher = Cipher.getInstance(myEncryptionScheme);
            try {
                key = skf.generateSecret(ks);
            } catch (InvalidKeySpecException ex) {
                System.err.println("Error: url utils - " + ex);
            }
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            System.err.println("Error: url utils - " + ex);
        }
    }

    /**
     * Decrypt the shared link
     *
     * @param encstr encrypted link
     * @return decrypted link
     */
    public String decrypt(String encstr) {

        if (encstr.length() > 12) {
            String tempcipher = encstr.substring(12);
            return indecrypt(new String(Base64.decodeBase64(tempcipher)));

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
            byte[] plainText = unencryptedString.getBytes(CONSTANT.UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = Base64.encodeBase64String(encryptedText);
        } catch (UnsupportedEncodingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
             System.err.println("Error: url utils - "+e);
        }
        return encryptedString;
    }

    /**
     * initialise link to be decoded
     *
     * @param encryptedString encrypted link
     * @return decrypted link
     */
    public String indecrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
             System.err.println("Error: url utils - "+e);
        }
        return decryptedText;
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
        return Base64.encodeBase64String(salt) + Base64.encodeBase64String(linkToShare.getBytes());
    }
    
    public String encodeURL(String url){
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error encode url "+URLUtils.class.getName()+"  "+ex);
           return url;
        }
    }

}
