package mcru.MinecraftingTools.Helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * Стандартное сообщение в панели событий приложения
 */
public class LogMessage
{
    public static final int MESSAGE_INFO = 0;
    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_ERROR = 2;
    public static final int MESSAGE = 3;
    
    private ArrayList <ContentElement> message = new ArrayList <>();
    private String msgClass = null;
    
    /**
     * Простое сообщение в лог
     * @param text текст сообщения
     * @param type тип сообщения
     */
    public LogMessage(String text, int type)
    {
        DateFormat formatter = new SimpleDateFormat(config.LogDateTimeFormat);
        switch (type)
        {
            case MESSAGE_INFO:
                msgClass = "info16 info-message";
                break;
            case MESSAGE_SUCCESS:
                msgClass = "OK16 success-message";
                break;
            case MESSAGE_ERROR:
                msgClass = "error16 error-message";
                break;
            case MESSAGE:
                msgClass = "bubble16 message";
                break;
            default:
                msgClass = null;
        }
        
        message.add(new ContentElement(String.format("[%s]   ", formatter.format(new Date())))
                            .addElementClass("date-time"));
        message.add(new ContentElement(text));
    }
    
    /**
     * Простое сообщение в лог через {@link ContentElement}
     * @param element элемент {@link ContentElement}
     * @param type    тип сообщения
     */
    public LogMessage(ContentElement element, int type)
    {
        DateFormat formatter = new SimpleDateFormat(config.LogDateTimeFormat);
        
        switch (type)
        {
            case MESSAGE_INFO:
                msgClass = "info16 info-message";
                break;
            case MESSAGE_SUCCESS:
                msgClass = "OK16 success-message";
                break;
            case MESSAGE_ERROR:
                msgClass = "error16 error-message";
                break;
            case MESSAGE:
                msgClass = "bubble16 message";
                break;
            default:
                msgClass = null;
        }
        
        message.add(new ContentElement(String.format("[%s]   ", formatter.format(new Date())))
                            .addElementClass("date-time"));
        message.add(element);
    }
    
    
    /**
     * Длинное сообщение в лог
     * @param elements список элементов {@link ContentElement}
     * @param type     тип сообщения
     */
    public LogMessage(ArrayList <ContentElement> elements, int type)
    {
        DateFormat formatter = new SimpleDateFormat(config.LogDateTimeFormat);
        
        switch (type)
        {
            case MESSAGE_INFO:
                msgClass = "info16 info-message";
                break;
            case MESSAGE_SUCCESS:
                msgClass = "OK16 success-message";
                break;
            case MESSAGE_ERROR:
                msgClass = "error16 error-message";
                break;
            case MESSAGE:
                msgClass = "bubble16 message";
                break;
            default:
                msgClass = null;
        }
        
        message.add(new ContentElement(String.format("[%s]   ", formatter.format(new Date())))
                            .addElementClass("date-time"));
        message.addAll(elements);
    }
    
    /**
     * Получить сообщение
     */
    public ArrayList <ContentElement> get()
    {
        return message;
    }
    
    /**
     * класс сообщения
     */
    public String getMsgClass()
    {
        return msgClass;
    }
}
