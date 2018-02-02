package mcru.MinecraftingTools.Functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import mcru.MinecraftingTools.MyApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * Класс для записи-чтения объектов из JSON файлов. Сделан на базе библиотеки https://github.com/google/gson
 */
public class JsonFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * Загрузить объект из файла JSON
     * @param fileName имя файла
     * @param oClass   класс объекта
     * @return прочитанный объект
     */
    public static Object loadObjectFormFile(String fileName, Object oClass)
    {
        logger.log(Level.INFO, String.format("Открываем файл: \"%1$s\"", fileName));
        
        try
        {
            String string = FileFunc.fileToString(fileName, config.Encoding);
            
            if (string == null)
                return null;
            
            Object object = new Gson().fromJson(string, oClass.getClass());
            
            logger.log(Level.INFO, String.format("Объект прочитан из \"%1$s\"", fileName));
            return object;
        }
        catch (JsonSyntaxException e)
        {
            logger.log(Level.SEVERE,
                       String.format("Объект прочитать не удалось из \"%1$s\", синтаксическая ошибка", fileName),
                       e);
        }
        
        return null;
    }
    
    /**
     * Сохранить объект в файл JSON
     * @param fileName полный путь до файла
     * @param object   объект для сохранения
     * @return результат: удачно/неудачно
     */
    public static boolean saveObjectToFile(String fileName, Object object)
    {
        
        if (fileName == null || fileName.isEmpty())
        {
            logger.log(Level.SEVERE, "Имя файла для сохранения объекта указано неверно");
            return false;
        }
        
        try
        {
            String string = new GsonBuilder().setPrettyPrinting().create().toJson(object);
            
            if (FileFunc.saveTextToFile(fileName, string, config.Encoding, false))
            {
                logger.log(Level.INFO, String.format("Объект записан успешно в \"%1$s\"", fileName));
                return true;
            }
            
            logger.log(Level.SEVERE, String.format("Объект записать не удалось в \"%1$s\"", fileName));
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, String.format("Объект записать не удалось в \"%1$s\"", fileName), e);
        }
        return false;
    }
    
    /**
     * получить объект из json-строки
     * @param string исходная строка {@link String}
     * @param oClass класс объекта
     * @return готовый объект
     */
    public static Object loadObjectFormString(String string, Object oClass)
    {
        if (string == null || string.isEmpty())
        {
            logger.log(Level.SEVERE, "JSON строка пустая или NULL");
            return null;
        }
        
        try
        {
            Object object = new Gson().fromJson(string, oClass.getClass());
            
            logger.log(Level.INFO, "Объект прочитан из JSON строки");
            return object;
        }
        catch (JsonSyntaxException e)
        {
            logger.log(Level.SEVERE, "Извлечь объект не удалось. Синтаксическая ошибка.", e);
        }
        return null;
    }
    
    /**
     * сохраняем объект в строку {@link String}
     * @param object объект для сохранения
     * @return готовая строка
     */
    public static String saveObjectToString(Object object)
    {
        if (object == null)
        {
            logger.log(Level.SEVERE, "Объект = NULL");
            return null;
        }
        
        try
        {
            String string = new GsonBuilder().create().toJson(object);
            
            logger.log(Level.INFO, String.format("Объект записан в строку успешно (%d)", string.length()));
            return string;
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Объект записать в строку не удалось", e);
        }
        return null;
    }
}
