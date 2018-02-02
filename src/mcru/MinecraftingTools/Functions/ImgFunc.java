package mcru.MinecraftingTools.Functions;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import mcru.MinecraftingTools.MyApplication;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Работа с графикой
 */
public class ImgFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * загрузить Image по URL
     * @param url               адрес
     * @param backgroundLoading фоновая загрузка вкл
     */
    public static Image LoadImageFromUrl(String url, boolean backgroundLoading)
    {
        return new Image(url, backgroundLoading);
    }
    
    /**
     * загрузить Image из base64-строки
     * @param imageString строка с изображением
     * @param scale       масштаб
     */
    public static Image LoadImageFromBase64(String imageString, int scale)
    {
        byte[] imageByte;
        
        if (imageString == null || imageString.isEmpty())
            return null;
        
        try
        {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Base64 в Image, ошибка ввода-вывода (1)", e);
            return null;
        }
        
        try (ByteArrayInputStream bis_size = new ByteArrayInputStream(imageByte);
             ByteArrayInputStream bis_base = new ByteArrayInputStream(imageByte))
        {
            Image img_size = new Image(bis_size);
            double height = img_size.getHeight() * scale;
            double width = img_size.getWidth() * scale;
            return new Image(bis_base, width, height, true, true);
        }
        catch (IllegalArgumentException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Base64 в Image, ошибка аргументов", e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Base64 в Image, ошибка ввода-вывода (2)", e);
        }
        
        return null;
    }
    
    /**
     * загрузить Image из base64-строки
     * @param imageString строка с изображением
     */
    public static Image LoadImageFromBase64(String imageString)
    {
        byte[] imageByte;
        
        if (imageString == null || imageString.isEmpty())
            return null;
        
        try
        {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Base64 в Image, ошибка ввода-вывода (1)", e);
            return null;
        }
        
        try (ByteArrayInputStream bis_base = new ByteArrayInputStream(imageByte))
        {
            return new Image(bis_base);
        }
        catch (IllegalArgumentException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Base64 в Image, ошибка аргументов", e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Base64 в Image, ошибка ввода-вывода (2)", e);
        }
        
        return null;
    }
    
    /**
     * конвертация Image в Base64
     * @param type тип графического ресурса ("png")
     */
    public static String LoadBase64FromImage(Image image, String type)
    {
        if (image == null)
            return null;
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bImage, type, baos);
            byte[] imageByte = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageByte);
        }
        catch (IllegalArgumentException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Image в Base64, ошибка аргументов", e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка при конвертировании Image в Base64, ошибка ввода-вывода", e);
        }
        
        return null;
    }
}
