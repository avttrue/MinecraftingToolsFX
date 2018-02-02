package mcru.MinecraftingTools.Functions;

import mcru.MinecraftingTools.MyApplication;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * Класс для работы с десктопом
 */
public class DesktopFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * Открыть файл средствами ОС
     */
    public static void exploreFile(File file)
    {
        Desktop desktop = Desktop.getDesktop();
        
        if (!file.exists())
        {
            logger.log(Level.SEVERE, String.format("Файл не найден: %1$s", file.getAbsolutePath()));
            return;
        }
        
        if (!desktop.isSupported(Desktop.Action.OPEN))
        {
            logger.log(Level.SEVERE, "Функционал \"Desktop.Action.OPEN\" не поддерживается");
            return;
        }
        
        new Thread(() -> {
            try
            {
                desktop.open(file);
                logger.log(Level.INFO,
                           String.format("Файл средствами ОС открыт успешно: \"%1$s\"", file.getAbsolutePath()));
            }
            catch (NullPointerException | IllegalArgumentException e)
            {
                logger.log(Level.SEVERE,
                           String.format("Файл открыть не удалось: \"%1$s\", файл = NULL или не найден",
                                         file.getAbsolutePath()),
                           e);
            }
            catch (UnsupportedOperationException e)
            {
                logger.log(Level.SEVERE,
                           String.format("Файл открыть не удалось: \"%1$s\", функционал не поддерживается",
                                         file.getAbsolutePath()),
                           e);
            }
            catch (SecurityException e)
            {
                logger.log(Level.SEVERE,
                           String.format("Файл открыть не удалось: \"%1$s\", недостаточно прав",
                                         file.getAbsolutePath()),
                           e);
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE,
                           String.format("Файл открыть не удалось: \"%1$s\" ошибка ввода-вывода",
                                         file.getAbsolutePath()),
                           e);
            }
        }).start();
    }
    
    /**
     * Открыть Веб-ресурс средствами ОС
     * @param url            URL
     * @param addDefProtocol подставлять "http://" в начало URL-а
     */
    public static void openWebResource(String url, boolean addDefProtocol)
    {
        Desktop desktop = Desktop.getDesktop();
        
        if (!desktop.isSupported(Desktop.Action.BROWSE))
        {
//            mainWindow.AddTextToLog(
//                    new Message("Функционал \"Desktop.Action.BROWSE\" не поддерживается\n", Main.config.EventErrorWebColor,
//                                true, true), true);
            return;
        }
        new Thread(() -> {
            try
            {
                String newURL = addDefProtocol && !url.contains("://") ? config.DefaultUrlProtocol + url : url;
                
                desktop.browse(URI.create(newURL));
                logger.log(Level.INFO, String.format("URL средствами ОС открыт успешно: \"%1$s\"", newURL));
            }
            catch (NullPointerException e)
            {
                logger.log(Level.SEVERE, "URL открыть не удалось: URL = NULL", e);
            }
            catch (IllegalArgumentException e)
            {
                logger.log(Level.SEVERE, "URL открыть не удалось: given string violates RFC 2396", e);
            }
            catch (UnsupportedOperationException e)
            {
                logger.log(Level.SEVERE, "URL открыть не удалось: функционал не поддерживается", e);
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, "URL открыть не удалось: ошибка ввода-вывода", e);
            }
            catch (SecurityException e)
            {
                logger.log(Level.SEVERE, "URL открыть не удалось: недостаточно прав", e);
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "URL открыть не удалось: неизвестная ошибка", e);
            }
        }).start();
    }
}
