package mcru.MinecraftingTools.Functions;

import javafx.scene.image.Image;
import mcru.MinecraftingTools.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Работа с ресурсами приложения
 */
public class ResFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * Получение графических ресурсов
     */
    public static Image getImage(String key)
    {
        Image image = null;
        
        if (key == null || key.isEmpty())
        {
            logger.log(Level.SEVERE, "Неверный формат ключа");
            return null;
        }
        
        try
        {
            image = new Image(MyApplication.class.getResourceAsStream(String.format("Res/Images/%1$s.png", key)));
        }
        catch (NullPointerException e)
        {
            logger.log(Level.SEVERE, "Ошибка при извлечении ресурса", e);
        }
        catch (IllegalFormatException e)
        {
            logger.log(Level.SEVERE, "Неверный формат ключа", e);
        }
        
        if (image == null)
            logger.log(Level.SEVERE, "Ключ не найден: " + key);
        
        return image;
    }
    
    /**
     * получить графический ресурс как ссылку
     */
    public static String getImageAsPath(String key)
    {
        String result = null;
        
        if (key == null || key.isEmpty())
        {
            logger.log(Level.SEVERE, "Неверный формат ключа");
            return null;
        }
        
        try
        {
            result = MyApplication.class.getResource(String.format("Res/Images/%1$s.png", key)).toString();
        }
        catch (NullPointerException e)
        {
            logger.log(Level.SEVERE, String.format("Ошибка при получении ресурса: \"%s\"", key), e);
        }
        catch (IllegalFormatException e)
        {
            logger.log(Level.SEVERE, "Неверный формат ключа", e);
        }
        
        if (result == null)
            logger.log(Level.SEVERE, String.format("Ключ не найден: %1$s", key));
        
        return result;
    }
    
    /**
     * Получение текстовых ресурсов (скриптов)
     */
    public static String getScript(String key)
    {
        String script = null;
        
        if (key == null || key.isEmpty())
        {
            logger.log(Level.SEVERE, "Неверный формат ключа");
            return null;
        }
        
        try (InputStream inputStream = MyApplication.class
                .getResourceAsStream(String.format("Res/Scripts/%1$s.js", key)))
        {
            script = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, String.format("Ошибка ввода-вывода при получения ресурса: \"%s\"", key), e);
        }
        catch (NullPointerException e)
        {
            logger.log(Level.SEVERE, String.format("Ошибка при получении ресурса: \"%s\"", key), e);
        }
        
        if (script == null)
        {
            logger.log(Level.SEVERE, String.format("Ключ не найден или скрипт пустой: %1$s", key));
            return null;
        }
        
        if (script.isEmpty())
            logger.log(Level.SEVERE, String.format("Скрипт пустой: %1$s", key));
        
        return script;
    }
}
