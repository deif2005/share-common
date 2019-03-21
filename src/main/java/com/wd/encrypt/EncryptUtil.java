package com.wd.encrypt;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AES加密工具类，仅用于 配置中心的启动配置串
 */
public class EncryptUtil {
    private static final byte[] encryptTag = new byte[]{83, 1};
    private static final char oneChar = 1;
    public static final String encryptStr = "S" + oneChar;
    public static final String encryptKey = "i love thinkoy, and i love change";

    /**
     *
     * @param datas 数据串
     * @return true  加密的串；  false 没有加密的串
     */
    public static boolean isEncrypt(byte[] datas){
        if(datas[0] == encryptTag[0] && datas[1] == encryptTag[1]){
            return true;
        }

        return false;
    }

    /**
     * 按照 加密规则 加密
     * @param cryptStr
     * @return  返回加密后的结果
     * @throws Exception
     */
    public static String encrypt(String cryptStr) throws Exception {
        return EncryptUtil.encryptStr + AESUtil.aesEncrypt(cryptStr, EncryptUtil.encryptKey);
    }

    /**
     * 解压加密串
     * @param encryptStr
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr) throws Exception {
        String realEncryptStr = encryptStr.substring(2);

        return AESUtil.aesDecrypt(realEncryptStr, encryptKey);
    }

    //public String text="";
    /**
     * @Description:使用MD5加密指定的字符串.
     * @param s
     * @return
     * @date  Aug 12, 2011
     * @modify
     */
    public final static String MD5(String s) {
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            return byteArrayToHex(mdTemp.digest());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        int j = byteArray.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = byteArray[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * 对文件计算md5值
     * @param file
     * @return
     * @throws IOException
     */
    public static String fileMd5(File file)  {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel ch =in.getChannel();
            MappedByteBuffer byteBuffer =ch.map(FileChannel.MapMode.READ_ONLY, 0,file.length());
            MessageDigest messagedigest  = MessageDigest.getInstance("MD5");
            messagedigest.update(byteBuffer);
            return byteArrayToHex (messagedigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e){
            throw  new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @Description:加密指定的整型数，通常用来加密ID值！
     * 编码的原理是根据当前的currentTimeMillis在指定的位置插入其ID值，然后再附加
     * ID值的长度来进行编码.System.currentTimeMillis获取的值为13位长度，即使100
     * 年后也是这样，所以这个基本上是不变的！
     * @param id
     * @return
     * @date  Aug 12, 2011
     * @modify
     */

    private final static int ENCODE_ID_INSERT_INDEX = 5; // 插入的索引位置，0-12之间

    public final static String encodeId(int id) {
        String _id = String.valueOf(id);
        if (id < 0 || _id.length() > 9)
            return "";
        StringBuilder buf = new StringBuilder();
        buf.append(System.currentTimeMillis());
        for (int i = 0; i < _id.length(); i++) {
            buf.insert(ENCODE_ID_INSERT_INDEX, _id.charAt(i));
        }
        buf.append(_id.length()); // 长度校验位
        return buf.toString();
    }


    /**
     * @Description:反编码ID后的字符串.<P>
     * 注意，编码和解码的数字长度不能超过9位！
     * @param str
     * @return
     * @date  Aug 12, 2011
     * @modify
     */
    public final static int unencodeId(String str) {
        String times = System.currentTimeMillis() + "";
        if (str == null || str.length() < times.length())
            return 0;
        if (!str.matches("\\d{13,}"))
            return 0;
        int len = Integer.parseInt(str.substring(str.length() - 1));
        if (str.length() != (times.length() + len + 1))
            return 0;
        return Integer.parseInt(new StringBuilder(str.substring(
                ENCODE_ID_INSERT_INDEX, ENCODE_ID_INSERT_INDEX + len))
                .reverse().toString());
    }

    /**
     * @Description計算二進制數據.<P>
     * @author 廖大劍
     * @date  2012-05-17
     */
    private static String byte2hex(byte[] b) {// 二行制转字符串

        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }
    /**
     * @Description:計算sha1值.<P>
     * @return sha1值
     * @author 廖大劍
     * @date  2012-05-17
     */
    public static String getSha1(String text){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("sha-1");
            byte[] byteText = text.getBytes();
            md.update(byteText);
            byte[] sha1 = md.digest();
            return byte2hex(sha1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DES3加密
     * @param mykey 密钥（二进制数组）
     * @param str   要加密的字符串
     * @return
     */
    public static byte[] des3Encrypt(byte[] mykey, String str) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(mykey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        byte[] b = cipher.doFinal(str.getBytes());
        return b;
    }

}
