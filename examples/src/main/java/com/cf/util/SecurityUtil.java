package com.cf.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ray on 6/4/14.
 */
public class SecurityUtil {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SecurityUtil.class);

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String ENC = "UTF-8";
    private static Base64 base64 = new Base64();
    public static boolean DEBUG = true;

    /**
     *
     * @param url
     *            the url for "request_token" URLEncoded.
     * @param params
     *            parameters string, URLEncoded.
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.InvalidKeyException
     */
    public static String getOAuthSignature(String secretKey, String method, String url, String params)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        if (method == null)
            method = "GET";

        /**
         * base has three parts, they are connected by "&": 1) protocol 2) URL
         * (need to be URLEncoded) 3) Parameter List (need to be URLEncoded).
         */
        StringBuilder base = new StringBuilder();
        base.append(method);
        base.append("&");
        base.append(url);
        base.append("&");
        base.append(params);

        LOGGER.debug("String for oauth_signature generation:" + base);


        byte[] keyBytes = secretKey.getBytes(ENC);
        SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(key);

        // encode it, base64 it, change it to string and return.
        return new String(base64.encode(mac.doFinal(base.toString().getBytes(ENC))), ENC).trim();
    }

}
