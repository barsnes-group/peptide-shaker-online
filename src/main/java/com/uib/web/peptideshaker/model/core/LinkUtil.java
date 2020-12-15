package com.uib.web.peptideshaker.model.core;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is utility for sharing links cross Web PeptideShaker users
 *
 * @author Yehia Farag
 */
public class LinkUtil {

   

    /**
     * Initialise the utility
     */
    public LinkUtil() {
       
    }

    

   
}
