package mcru.MinecraftingTools.Helpers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mcru.MinecraftingTools.Functions.ResFunc;
import ontando.minecrafting.remote_access.env.DataValue;

import java.util.Collections;
import java.util.Map;

import static mcru.MinecraftingTools.Functions.TextFunc.DecodePlayerOnlineFlags;
import static mcru.MinecraftingTools.Functions.TextFunc.DecodePlayerStatusFlags;

/**
 * Класс элемента списка игроков <br>
 */
public class PlayerListElement implements Comparable <PlayerListElement>
{
    public String nick;
    public String uuid;
    public int online_flags;
    public int status_flags;
    public long id = -1;
    public boolean alert = false;
    public Image image = new ImageView(ResFunc.getImage("black_user16")).getImage();
    private Map <String, DataValue> properties = Collections.emptyMap();
    private Map <String, DataValue> fields = Collections.emptyMap();
    
    public PlayerListElement(long id, String nick, String uuid)
    {
        this.id = id;
        this.nick = nick;
        this.uuid = uuid;
    }
    
    public final String toString()
    {
        String onlineFlags = DecodePlayerOnlineFlags(this.online_flags);
        String statusFlags = DecodePlayerStatusFlags(this.status_flags);
        
        return String.format("%1$s [ID:%2$d] / %3$s / %4$s", nick, this.id, onlineFlags, statusFlags);
    }
    
    @Override
    public int compareTo(PlayerListElement ple)
    {
        return ple == null ? 1 : Long.compare(this.id, ple.id);
    }
    
    /**
     * Используется для сортировки, получение статуса игрока
     */
    public boolean isOnline()
    {
        return ((online_flags & 0x01) == 0);
    }
    
    /**
     * Используется для сортировки, получение ID игрока
     */
    public long getId()
    {
        return id;
    }
    
    /**
     * Используется для сортировки, получение ника игрока
     */
    public String getNick()
    {
        return nick;
    }
    
    /**
     * Используется для сортировки, получение тревоги игрока
     */
    public boolean isAlert()
    {
        return alert;
    }
    
    /**
     * получить поля
     */
    public Map <String, DataValue> getFields()
    {
        return fields;
    }
    
    /**
     * установить поля
     */
    public void setFields(Map <String, DataValue> fields)
    {
        this.fields = fields;
    }
    
    /**
     * получить свойства
     */
    public Map <String, DataValue> getProperties()
    {
        return properties;
    }
    
    /**
     * установить свойства
     */
    public void setProperties(Map <String, DataValue> properties)
    {
        this.properties = properties;
    }
}
