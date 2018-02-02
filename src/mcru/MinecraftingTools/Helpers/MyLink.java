package mcru.MinecraftingTools.Helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * класс-обёртка для ссылок
 */
public class MyLink
{
    private Map <String, String> link = new HashMap <>();
    
    public MyLink(String key, String value)
    {
        link.put(key, value);
    }
    
    public Map <String, String> get()
    {
        return link;
    }
    
    public Map <String, String> put(String key, String value)
    {
        link.put(key, value);
        return link;
    }
    
    public void clear()
    {
        link.clear();
    }
}
