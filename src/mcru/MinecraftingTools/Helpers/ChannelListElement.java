package mcru.MinecraftingTools.Helpers;

import javafx.scene.image.Image;
import ontando.minecrafting.remote_access.env.DataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Класс элемента списка каналов<br>
 */
public class ChannelListElement implements Comparable <ChannelListElement>
{
    public String name = "";
    public long id = -1;
    public String type = "";
    public Map <String, DataValue> properties = Collections.emptyMap();
    public Map <String, DataValue> fields = Collections.emptyMap();
    public ArrayList <Long> playersID = new ArrayList <>();
    public Image image = null;
    
    public ChannelListElement(long id, String type, String name)
    {
        this.name = name;
        this.id = id;
        this.type = type;
    }
    
    public final String toString()
    {
        return String.format("%1$s [%2$d] / %3$s / ID:%4$d", name, playersID.size(), type, this.id);
    }
    
    public final void AddPlayerToList(long player_id)
    {
        RemovePlayerFromList(player_id);
        playersID.add(player_id);
        playersID.sort(null);
    }
    
    public final void RemovePlayerFromList(long player_id)
    {
        playersID.remove(player_id);
    }
    
    @Override
    public int compareTo(ChannelListElement cle)
    {
        return cle == null ? 1 : Long.compare(this.id, cle.id);
    }
    
    /**
     * Используется для сортировки, получение названия канала
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Используется для сортировки, получение ID канала
     */
    public long getId()
    {
        return id;
    }
    
    /**
     * Используется для сортировки, получение типа канала
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Используется для сортировки, получение количества игроков на канале
     */
    public int getPlayersCount()
    {
        return playersID.size();
    }
}
