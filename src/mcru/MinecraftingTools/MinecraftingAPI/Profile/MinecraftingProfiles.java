package mcru.MinecraftingTools.MinecraftingAPI.Profile;

import mcru.MinecraftingTools.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * БД профилей игроков Minecrafting.ru
 */
public class MinecraftingProfiles
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    /**
     * буфер для складывания временных принятых данных в <br>
     * {@link mcru.MinecraftingTools.MinecraftingAPI.Handlers.UserWhoIsSanctionHandler}
     */
    public ArrayList <Sanction> SanctionsBuffer = new ArrayList <>();
    /**
     * буфер для складывания временных принятых данных в <br>
     * {@link mcru.MinecraftingTools.MinecraftingAPI.Handlers.UserWhoIsIpHandler}
     */
    public Map <String, Integer> IPBuffer = new HashMap <>();
    /**
     * хранилище - список провилей
     */
    public ArrayList <MinecraftingProfile> profiles = new ArrayList <>();
    
    /**
     * Удалить запись из БД по UUID
     */
    public boolean remove(String uuid)
    {
        for (MinecraftingProfile mp : profiles)
        {
            if (mp.UUID.equals(uuid))
            {
                if (profiles.remove(mp))
                {
                    logger.log(Level.INFO, String.format("Запись удалена, UUID = %1$s", uuid));
                    return true;
                }
                else
                {
                    logger.log(Level.INFO, String.format("Запись удалить не удалось, UUID = %1$s", uuid));
                    return false;
                }
            }
        }
        logger.log(Level.INFO, String.format("Запись не найдена, UUID = %1$s", uuid));
        return false;
    }
    
    /**
     * очистить буфер
     */
    public final void clearBuffer()
    {
        SanctionsBuffer = new ArrayList <>();
        IPBuffer = new HashMap <>();
    }
    
    /**
     * добавить запись {@link MinecraftingProfile} используя данные буфера
     */
    public void addUseBuffer(MinecraftingProfile minecraftingProfile)
    {
        MinecraftingProfile mp = new MinecraftingProfile();
        
        mp.UUID = minecraftingProfile.UUID;
        mp.Nick = minecraftingProfile.Nick;
        mp.LongDateTime = minecraftingProfile.LongDateTime;
        
        mp.IPList.clear();
        mp.SanctionsList.clear();
        mp.IPList.putAll(IPBuffer);
        
        for (Sanction s : SanctionsBuffer)
        {
            mp.SanctionsList.add(s.clone());
        }
        
        clearBuffer();
        remove(minecraftingProfile.UUID);
        
        if (profiles.add(mp))
            logger.log(Level.INFO, String.format("Запись добавлена, UUID = %1$s", mp.UUID));
        else
            logger.log(Level.SEVERE,
                       String.format("Запись добавить не удалось, UUID = %1$s", minecraftingProfile.UUID));
    }
    
    /**
     * Получить размер БД
     */
    public final int size()
    {
        return profiles.size();
    }
    
    /**
     * Найти запись в БД по UUID
     */
    public MinecraftingProfile find(String uuid)
    {
        for (MinecraftingProfile mp : profiles)
        {
            if (mp.UUID.equals(uuid))
                return mp;
        }
        return null;
    }
}
