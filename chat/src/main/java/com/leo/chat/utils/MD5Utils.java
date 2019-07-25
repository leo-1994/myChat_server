package com.leo.chat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class MD5Utils {

    public static MessageDigest md5 = null;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Description: 对字符串进行md5加密
     */
    public static String getMD5Str(String strValue) {
        return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
    }

}
