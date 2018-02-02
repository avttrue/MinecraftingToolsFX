package mcru.MinecraftingTools.Functions;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.MyApplication;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Класс для работы с текстом
 */
public class TextFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * простой поиск вхождения в тексте строки-регекспа
     */
    public static boolean applySimpleRegExp(String text, String regexp)
    {
        if (regexp == null || regexp.isEmpty())
        {
            //logger.log(Level.SEVERE, "Некорректные параметры: регулярное выражение = NULL или пустое");
            return false;
        }
        
        if (text == null || text.isEmpty())
        {
            //logger.log(Level.SEVERE, "Некорректные параметры: текст = NULL или пустой");
            return false;
        }
        
        try
        {
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(text);
            return m.matches();
        }
        catch (PatternSyntaxException e)
        {
            Platform.runLater(() -> scene.logContent
                    .addMessage(new LogMessage("Ошибка в регулярном выражении: %1$s", LogMessage.MESSAGE_ERROR)));
            logger.log(Level.SEVERE, "Ошибка в регулярном выражении", e);
            return false;
        }
    }
    
    /**
     * пословный анализ текста через регекспы
     * @param text   текст
     * @param regexp регексп
     */
    public static TreeMap <Integer, String> applyRegExp(String text, String regexp)
    {
        TreeMap <Integer, String> result = new TreeMap <>();
        
        if (regexp == null || regexp.isEmpty())
        {
            //logger.log(Level.SEVERE, "Некорректные параметры: регулярное выражение = NULL или пустое");
            return result;
        }
        
        if (text == null || text.isEmpty())
        {
            //logger.log(Level.SEVERE, "Некорректные параметры: текст = NULL или пустой");
            return result;
        }
        
        String[] lines = text.replace("\n", "").split(" ");
        
        for (String s : lines)
        {
            try
            {
                Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher m = p.matcher(s);
                if (m.matches())
                {
                    result.put(text.indexOf(s), s);
                    logger.log(Level.INFO, String.format("Сработал регексп по тексту \"%1$s\"", s));
                }
                
            }
            catch (PatternSyntaxException e)
            {
                Platform.runLater(() -> scene.logContent
                        .addMessage(new LogMessage("Ошибка в регулярном выражении: %1$s", LogMessage.MESSAGE_ERROR)));
                logger.log(Level.SEVERE, "Ошибка в регулярном выражении", e);
                return null;
            }
        }
        
        return result;
    }
    
    /**
     * перевод размера в байтах в читаемый вид
     * @param bytes длина в байтах
     * @param si    использовать СИ
     */
    public static String humanReadableByteCount(long bytes, boolean si)
    {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    /**
     * перевод милисекунд в читаемый вид
     * @param time   время в милисекундах
     * @param needMS добавлять милисекунды?
     */
    public static String TimeIntervalToString(long time, boolean needMS)
    {
        long t = !needMS ? time * 1000 : time;
        
        long d = t / 86400000;
        long h = (t - d * 86400000) / 3600000;
        long m = (t - d * 86400000 - h * 3600000) / 60000;
        long s = (t - d * 86400000 - h * 3600000 - m * 60000) / 1000;
        
        if (needMS)
        {
            long ms = t - d * 86400000 - h * 3600000 - m * 60000 - s * 1000;
            return String.format("%1$dд. %2$dч. %3$02dм. %4$02dс. %5$03dмс.", d, h, m, s, ms);
        }
        else
            return String.format("%1$dд. %2$dч. %3$02dм. %4$02dс.", d, h, m, s);
    }
    
    /**
     * получить строковое значение даты-времени из long
     */
    public static String DateTimeToString(long datetime)
    {
        return new SimpleDateFormat(config.CommonDateTimeFormat).format(datetime);
    }
    
    /**
     * получить строковое значение даты-времени из long
     */
    public static String MessageDateToString(long datetime)
    {
        return new SimpleDateFormat(config.MessageDateFormat).format(datetime);
    }
    
    /**
     * получить строковое значение даты-времени из long
     */
    public static String MessageTimeToString(long datetime)
    {
        return new SimpleDateFormat(config.MessageTimeFormat).format(datetime);
    }
    
    /**
     * перевести серверное время в строковую дату
     * @param timestamp серверное время
     */
    public static String GetServerMessageDate(long timestamp)
    {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of(config.TimeZoneOffset));
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(config.MessageDateFormat);
        return ldt.format(formatterDate);
    }
    
    /**
     * перевести серверное время в строковое время
     * @param timestamp серверное время
     */
    public static String GetServerMessageTime(long timestamp)
    {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of(config.TimeZoneOffset));
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(config.MessageTimeFormat);
        return ldt.format(formatterTime);
    }
    
    /**
     * Преобразовать {@link Color} в формат пригодный для css
     */
    public static String ColorToRGBCode(Color color)
    {
        if (color == null)
            return "";
        
        return String.format("%02X%02X%02X",
                             (int) (color.getRed() * 255),
                             (int) (color.getGreen() * 255),
                             (int) (color.getBlue() * 255));
    }
    
    /**
     * узнать количество строк в тексте
     */
    public static int getLinesCount(String str)
    {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }
    
    /**
     * Получить строку заполненную символами
     * @param length длина строки в символах
     * @param symbol символ для заполнения
     */
    public static String GetSpace(int length, char symbol)
    {
        return new String(new char[length]).replace('\0', symbol);
    }
    
    /**
     * расшифровать online_flags для игрока
     */
    public static String DecodePlayerOnlineFlags(int online_flags)
    {
        String onlineFlags = "";
        
        if ((online_flags & 0x01) != 0) // онлайн ли вообще
        {
            onlineFlags += " online";
            
            if ((online_flags & 0x02) != 0) //  на ваниле
                onlineFlags += " vanilla";
            
            if ((online_flags & 0x04) != 0) //  invisible
                onlineFlags += " invisible";
            
            if ((online_flags & 0x08) != 0) //  hide
                onlineFlags += " hide";
        }
        else
            onlineFlags = " offline";
        
        return onlineFlags;
    }
    
    /**
     * расшифровать status_flags для игрока
     */
    public static String DecodePlayerStatusFlags(int status_flags)
    {
        String statusFlags = "";
        
        if ((status_flags & 0x01) != 0) // afk
            statusFlags += " afk";
        
        if ((status_flags & 0x02) != 0) //  guest
            statusFlags += " guest";
        
        if ((status_flags & 0x04) != 0) //  freeze
            statusFlags += " freeze";
        
        if ((status_flags & 0x08) != 0) //  mute
            statusFlags += " mute";
        
        if ((status_flags & 0x10) != 0) //  curse
            statusFlags += " curse";
        
        if ((status_flags & 0x20) != 0) //  bane
            statusFlags += " bane";
        
        return statusFlags;
    }
    
    /**
     * обрезать строку до указанного размера
     * @param text     сам текст
     * @param maxvalue максимально допустимый размер текста
     */
    public static String SetFixedSize(String text, int maxvalue)
    {
        if (text == null)
            return "";
        
        if (text.length() > maxvalue)
            return text.substring(0, maxvalue) + "...";
        else
            return text;
    }
    
    /**
     * преобразовать путь до файла в формат вида file://
     */
    public static String PathToURL(String path)
    {
        try
        {
            return new File(path).toURI().toURL().toString();
        }
        catch (MalformedURLException e)
        {
            logger.log(Level.SEVERE, "Некорректный путь указан", e);
        }
        catch (IllegalArgumentException e)
        {
            logger.log(Level.SEVERE, "Некорректные аргументы указаны", e);
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, "Недостаточно прав", e);
        }
        catch (NullPointerException e)
        {
            logger.log(Level.SEVERE, "Путь = NULL?", e);
        }
        return null;
    }
    
    /**
     * преобразовать путь до файла в формат пригодный для использования в HTML документах <br>
     * http://www.weblabla.ru/reference/html/url_encode.html
     */
    public static String SetPathToHTML(String path)
    {
        try
        {
            return path.replace("\\", "\\\\").replace(" ", "%20");
        }
        catch (NullPointerException e)
        {
            logger.log(Level.SEVERE, "Путь = NULL?", e);
        }
        return null;
    }
    
    /**
     * проверка, что строка - валидный Integer
     */
    public static boolean StringIsInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException | NullPointerException ignored)
        {
        }
        
        return false;
    }
}
