package mcru.MinecraftingTools.Functions;

import mcru.MinecraftingTools.MyApplication;

import javax.crypto.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author http://cloud-notes.blogspot.ru/2011/02/java-c.html
 */
public class CryptoFunc
{
    private static SecretKey mySecretKey;
    private static Cipher eCipher;
    private static Cipher dCipher;
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    static
    {
        try
        {
            mySecretKey = new MySecretKey(null);
            eCipher = Cipher.getInstance("DES");
            dCipher = Cipher.getInstance("DES");
            eCipher.init(Cipher.ENCRYPT_MODE, mySecretKey);
            dCipher.init(Cipher.DECRYPT_MODE, mySecretKey);
        }
        catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e)
        {
            logger.log(Level.SEVERE, null, e);
        }
    }
    
    public CryptoFunc(byte[] key)
    {
        try
        {
            mySecretKey = new MySecretKey(key);
            eCipher = Cipher.getInstance("DES");
            dCipher = Cipher.getInstance("DES");
            eCipher.init(Cipher.ENCRYPT_MODE, mySecretKey);
            dCipher.init(Cipher.DECRYPT_MODE, mySecretKey);
        }
        catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e)
        {
            logger.log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Функция шифрования <br>
     * @param str строка открытого текста
     * @return зашифрованная строка в формате Base64
     */
    public static String encrypt(String str)
    {
        try
        {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = eCipher.doFinal(utf8);
            return new sun.misc.BASE64Encoder().encode(enc);
        }
        catch (IllegalBlockSizeException | UnsupportedEncodingException | BadPaddingException e)
        {
            logger.log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    /**
     * Функция расшифрования <br>
     * @param str зашифрованная строка в формате Base64
     * @return расшифрованная строка
     */
    public static String decrypt(String str)
    {
        try
        {
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            byte[] utf8 = dCipher.doFinal(dec);
            
            return new String(utf8, "UTF8");
            
        }
        catch (IllegalBlockSizeException | IOException | BadPaddingException e)
        {
            logger.log(Level.SEVERE, null, e);
        }
        return "";
    }
    
    /**
     * Ключ, не должен иметь длину более 8 байт
     */
    private static class MySecretKey implements SecretKey
    {
        private byte[] key = new byte[] {1, 2, 0, 4, 1, 9, 6, 2};
        
        public MySecretKey(byte[] key)
        {
            if (key != null && key.length == 8)
                this.key = key;
        }
        
        public String getAlgorithm()
        {
            return "DES";
        }
        
        public String getFormat()
        {
            return "RAW";
        }
        
        public byte[] getEncoded()
        {
            return key;
        }
    }
}