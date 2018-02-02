package mcru.MinecraftingTools.MojangAPI;

import mcru.MinecraftingTools.MyApplication;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * локальная БД профилей игроков из Mojang API
 * это массив {@link MojangProfile}
 * @see MojangProfileWebSearcher
 */
public class MojangProfiles
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    public ArrayList <MojangProfile> profiles = new ArrayList <>();
    
    /**
     * Удалить запись из БД по UUID
     */
    public final boolean remove(String uuid)
    {
        for (MojangProfile mp : profiles)
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
        logger.log(Level.WARNING, String.format("Запись не найдена, UUID = %1$s", uuid));
        return false;
    }
    
    /**
     * добавить запись в БД
     */
    public final boolean add(MojangProfile mojangProfile)
    {
        if (profiles.add(mojangProfile))
        {
            logger.log(Level.INFO, String.format("Запись добавлена, UUID = %1$s", mojangProfile.UUID));
            return true;
        }
        else
        {
            logger.log(Level.SEVERE, String.format("Запись добавить не удалось, UUID = %1$s", mojangProfile.UUID));
            return false;
        }
    }
    
    /**
     * получить количество записей
     */
    public final int size()
    {
        return profiles.size();
    }
    
    /**
     * найти профиль игрока по uuid
     */
    public final MojangProfile find(String uuid)
    {
        for (MojangProfile mProfile : profiles)
        {
            if (mProfile.UUID.equals(uuid))
                return mProfile;
        }
        return null;
    }
}


