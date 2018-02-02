package mcru.MinecraftingTools.MinecraftingAPI.Profile;

import mcru.MinecraftingTools.Functions.TextFunc;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * элемент локальной БД профилей игроков Minecrafting.ru {@link MinecraftingProfiles}
 */
public class MinecraftingProfile
{
    public String UUID = "";
    public String Nick = "";
    public long LongDateTime;
    public TreeMap <String, Integer> IPList = new TreeMap <>();
    public ArrayList <Sanction> SanctionsList = new ArrayList <>();
    
    /**
     * преобразовать в String используя все данные и адреса и количество заходов с них
     */
    public String toStringIPListFool()
    {
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry <String, Integer> e : IPList.entrySet())
        {
            result.append(String.format("%1$-22s(%2$d раз)\n", e.getKey(), e.getValue()));
        }
        
        return result.toString();
    }
    
    /**
     * преобразовать в String используя только адреса
     */
    public String toStringIPList()
    {
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry <String, Integer> e : IPList.entrySet())
        {
            result.append(String.format("%1$s\n", e.getKey()));
        }
        
        return result.toString();
    }
    
    /**
     * преобразовать в String
     */
    public String toStringSanctionList()
    {
        StringBuilder result = new StringBuilder();
        
        for (Sanction s : SanctionsList)
        {
            result.append(String.format("%1$s\n" + TextFunc.GetSpace(10, '-') + "\n", s.toString()));
        }
        
        return result.toString();
    }
}
