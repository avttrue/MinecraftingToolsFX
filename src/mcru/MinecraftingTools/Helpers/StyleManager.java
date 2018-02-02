package mcru.MinecraftingTools.Helpers;

import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.MyApplication;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Functions.TextFunc.PathToURL;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Класс для работы со стилями
 * http://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html
 */
public class StyleManager
{
    private static final String DEFAULT_STYLE = "Res/Styles/style.css";
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * Загрузить стиль CSS в основную форму из файла(если указан) или из ресурсов (если NULL)
     * @param filePath путь до файла css
     */
    private static void loadMyCSS(String filePath)
    {
        scene.getStylesheets().clear();
        scene.getRoot().setStyle(getBaseStyle());
        
        if (!config.UseCSS)
            return;
        
        if (filePath == null || filePath.isEmpty())
        {
            if (scene.getStylesheets().
                    add(MyApplication.class.getResource(DEFAULT_STYLE).toExternalForm()))
                logger.log(Level.INFO, String.format("Стиль \"%s\" загружен успешно", DEFAULT_STYLE));
            else
                logger.log(Level.SEVERE, String.format("Стиль \"%s\" загрузить не удалось", DEFAULT_STYLE));
        }
        else
        {
            String path = PathToURL(filePath);
            if (path != null && scene.getStylesheets().add(path))
                logger.log(Level.INFO, String.format("Стиль \"%s\" загружен успешно", path));
            else
                logger.log(Level.SEVERE, String.format("Стиль \"%s\" загрузить не удалось", path));
        }
    }
    
    /**
     * Загрузить стиль CSS в панель диалога {@link DialogPane} из файла(если указан) или из ресурсов (если NULL)
     * @param dialogPane панель диалога
     * @param filePath   путь до файла css
     */
    private static void loadMyCSS(DialogPane dialogPane, String filePath)
    {
        dialogPane.getStylesheets().clear();
        dialogPane.setStyle(getBaseStyle());
        
        if (!config.UseCSS)
            return;
        
        if (filePath == null || filePath.isEmpty())
        {
            if (dialogPane.getStylesheets().
                    add(MyApplication.class.getResource(DEFAULT_STYLE).toExternalForm()))
                logger.log(Level.INFO, String.format("Стиль \"%s\" загружен успешно", DEFAULT_STYLE));
            else
                logger.log(Level.SEVERE, String.format("Стиль \"%s\" загрузить не удалось", DEFAULT_STYLE));
        }
        else
        {
            String path = PathToURL(filePath);
            if (path != null && dialogPane.getStylesheets().add(path))
                logger.log(Level.INFO, String.format("Стиль \"%s\" загружен успешно", path));
            else
                logger.log(Level.SEVERE, String.format("Стиль \"%s\" загрузить не удалось", path));
        }
    }
    
    /**
     * получить основную тему оформления приложения
     */
    public static String getBaseStyle()
    {
        return String.format(config.CommonCSS,
                             TextFunc.ColorToRGBCode(Color.web(config.ThemeWebColor)),
                             TextFunc.ColorToRGBCode(Color.web(config.ThemeWebColor).darker()),
                             TextFunc.ColorToRGBCode(Color.web(config.ThemeWebColor).brighter()),
                             (int) config.CommonFontSize);
    }
    
    /**
     * Проверить и загрузить стиль CSS в основную форму
     */
    public static void checkAndLoadCSS()
    {
        String cssPatn = config.CSSFilePath;
        
        if (cssPatn == null || cssPatn.isEmpty())
        {
            loadMyCSS(null);
            return;
        }
        
        File file = new File(cssPatn);
        if (!file.exists() || file.isDirectory())
        {
            loadMyCSS(null);
            return;
        }
        
        loadMyCSS(cssPatn);
    }
    
    /**
     * Проверить и загрузить стиль CSS в панель диалога {@link DialogPane}
     * @param dialogPane панель диалога
     */
    public static void checkAndLoadCSS(DialogPane dialogPane)
    {
        String cssPatn = config.CSSFilePath;
        
        if (cssPatn == null || cssPatn.isEmpty())
        {
            loadMyCSS(dialogPane, null);
            return;
        }
        
        File file = new File(cssPatn);
        if (!file.exists() || file.isDirectory())
        {
            loadMyCSS(dialogPane, null);
            return;
        }
        
        loadMyCSS(dialogPane, cssPatn);
    }
}
