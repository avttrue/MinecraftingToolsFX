package mcru.MinecraftingTools.MinecraftingAPI.Authentification;

import mcru.MinecraftingTools.MinecraftingAPI.TokenFunc;
import mcru.MinecraftingTools.MyApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * список всех аутентификационных данных {@link AuthentificationData}
 */
public class AuthentificationList
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    /**
     * время последнего изменения
     */
    public long LastUpdate;
    /**
     * список аутентификаций {@link AuthentificationData}
     */
    private ArrayList <AuthentificationData> List = new ArrayList <>();
    
    /**
     * ищем аутентификацию по имени сервера
     */
    public AuthentificationData getAuthentification(String server)
    {
        for (AuthentificationData a : this.List)
        {
            if (a.getServerName().equals(server))
                return a;
        }
        // не нашли
        return new AuthentificationData();
    }
    
    /**
     * удалить аутентификацию по имени сервера
     */
    public final void removeAuthentification(String server)
    {
        for (AuthentificationData a : this.List)
        {
            if (a.getServerName().equals(server))
            {
                this.List.remove(a);
                logger.log(Level.INFO,
                           String.format("Удалены успешно аутентификационные данные для сервера \"%1$s\"", server));
                LastUpdate = new Date().getTime();
                return;
            }
        }
        logger.log(Level.WARNING, String.format("Данные для сервера \"%1$s\" не обнаружены", server));
    }
    
    /**
     * очистить аутентификационные данные
     */
    public final void clearAuthentification(String server)
    {
        for (AuthentificationData a : this.List)
        {
            if (a.getServerName().equals(server))
            {
                a.setPassword("");
                a.setUserName("");
                a.setServerAuthInformation(null);
                a.setLocalToken(TokenFunc.generateTokenByUUID(config.ServerTokenLength));
                logger.log(Level.INFO, String.format("Удалены аутентификационные данные для сервера %s", server));
                logger.log(Level.INFO, String.format("Присвоен локальный токен: %s", a.getLocalToken()));
                LastUpdate = new Date().getTime();
                return;
            }
        }
        logger.log(Level.WARNING, String.format("Данные для сервера \"%1$s\" не обнаружены", server));
    }
    
    /**
     * добавить аутентификацию в список
     */
    public final void addAuthentification(AuthentificationData newAuthentificationData)
    {
        removeAuthentification(newAuthentificationData.getServerName());
        if (List.add(newAuthentificationData))
        {
            logger.log(Level.INFO,
                       String.format("Добавлены успешно аутентификационные данные для сервера \"%1$s\"",
                                     newAuthentificationData.getServerName()));
            LastUpdate = new Date().getTime();
        }
    }
    
    public long getLastUpdate()
    {
        return LastUpdate;
    }
    
    public ArrayList <AuthentificationData> getList()
    {
        return List;
    }
    
    public void setList(ArrayList <AuthentificationData> list)
    {
        List = list;
        LastUpdate = new Date().getTime();
    }
}
