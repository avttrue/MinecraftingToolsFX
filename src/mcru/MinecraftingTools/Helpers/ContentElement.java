package mcru.MinecraftingTools.Helpers;

import mcru.MinecraftingTools.Interface.ContentPane;

import java.util.Map;

/**
 * Элемент для размещения в {@link ContentPane}
 */
public class ContentElement
{
    public String text = null;
    /**
     * Ссылка на ресурс <br>
     * Ключ:        [file | url | channel | player] <br>
     * Значение:    [путь | ссылка | id | id]
     */
    public Map <String, String> link;
    private String elementClass = null;
    
    /**
     * Конструктор для простой ссылки
     * @param text текстовое значение
     * @param link ссылка
     */
    public ContentElement(String text, Map <String, String> link)
    {
        this.text = text;
        this.link = link;
    }
    
    /**
     * Конструктор для простого текста
     * @param text текстовое значение
     */
    public ContentElement(String text)
    {
        this.text = text;
    }
    
    /**
     * добавить/установить класс документа
     */
    public ContentElement addElementClass(String elementClass)
    {
        if (this.elementClass == null)
            this.elementClass = elementClass;
        else
            this.elementClass += " " + elementClass;
        
        return this;
    }
    
    /**
     * получить класс элемента
     */
    public String getElementClass()
    {
        return elementClass;
    }
}
