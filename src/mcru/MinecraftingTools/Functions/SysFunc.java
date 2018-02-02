package mcru.MinecraftingTools.Functions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Класс системных функций
 */
public class SysFunc
{
    /**
     * получить список временных зон
     */
    public static String[] getTimeZoneList()
    {
        Set <String> allZones = ZoneId.getAvailableZoneIds();
        java.util.List <String> zoneList = new ArrayList <>();
        
        for (String s : allZones)
        {
            ZoneId zone = ZoneId.of(s);
            LocalDateTime dt = LocalDateTime.now();
            ZonedDateTime zdt = dt.atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            
            if (zoneList.indexOf(offset.toString()) < 0)
                zoneList.add(offset.toString());
        }
        
        Collections.sort(zoneList);
        String[] list = new String[zoneList.size()];
        list = zoneList.toArray(list);
        
        return list;
    }
    
    /**
     * получить список шрифтов в системе
     */
    public static String[] getFontFamilies()
    {
        java.util.List <String> fontFamilyNames = new ArrayList <>(javafx.scene.text.Font.getFamilies());
        
        String[] list = new String[fontFamilyNames.size()];
        list = fontFamilyNames.toArray(list);
        return list;
    }
    
    /**
     * получить имя дефолтного шрифта
     */
    public static String getDefaultFontName()
    {
        return javafx.scene.text.Font.getDefault().getFamily();
    }
    
    /**
     * проверить есть ли шрифт c именем name в списке системных шрифтов
     * @param name имя проверяемого на наличие шрифта
     */
    public static boolean checkFontFamily(String name)
    {
        java.util.List <String> fontFamilyNames = new ArrayList <>(javafx.scene.text.Font.getFamilies());
        return fontFamilyNames.contains(name);
    }
}
