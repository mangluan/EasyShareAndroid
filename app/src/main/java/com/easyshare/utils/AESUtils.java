package com.easyshare.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.net.URLDecoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密
 */
public class AESUtils {

    // 算法/模式/填充
    private static final String CipherMode = "AES/CBC/PKCS5Padding";
    // 约定的key
    private static final String key = "RCMX1kDD7I4nVp79";
    // 约定的iv
    private static final String iv = "kPTrXtK331rf2JWe";

    /***************************************************************************/

    /**
     * 将二进制转换成16进制
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /****************************************************************************/

    // 加密字符串数据, 返回的字节数据还需转化成16进制字符串
    public static String encrypt(String content) {
        if (TextUtils.isEmpty(content)) return "";
        return parseByte2HexStr(encrypt(content.getBytes()));
    }

    // 加密字节数据, 被加密的数据需要提前转化成字节格式
    private static byte[] encrypt(byte[] content) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /****************************************************************************/

    // 解密密字符串数据,
    public static String decrypt(String content) {
        if (TextUtils.isEmpty(content)) return "";
        return new String(decrypt(parseHexStr2Byte(content)));
    }

    // 解密字节数组
    private static byte[] decrypt(byte[] content) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES-128-CBC");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /****************************************************************************/

    /**
     * 参数解密
     *
     * @param datas 加密参数
     * @return 解密参数
     */
    public synchronized static String decryptParam(String datas) {
        if (datas == null) return "";
        // beas转码  aes解密
        byte[] bytes = decrypt(Base64.decode(datas, Base64.NO_WRAP));
        return URLDecoder.decode(new String(bytes));
    }


}