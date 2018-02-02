package mcru.MinecraftingTools.MinecraftingAPI;

import ontando.minecrafting.remote_access.env.DataValue;

import java.util.Collections;
import java.util.Map;

/**
 * сообщение в чат от сервера
 */
public class MC_Message
{
    // типы сообщений
    public static final int SIMPLE_MESSAGE = 0;
    public static final int INFORMATION = 1;
    public static final int INCOMING = 2;
    public static final int OUTCOMING = 3;
    public static final int FROM_PLUGIN = 4;
    public static final int CHAT_MESSAGE = 5;
    // важная информация о сообщении
    public String date;
    public String time;
    public String source;
    public String message;
    public long opponent_id = -1;
    public long author_id = -1;
    public long channel_id = -1;
    public long id = -1;
    public long associated_user = -1;
    
    //тип сообщения
    public int type = 0;
    
    // полная информация о сообщении
    public Map <String, DataValue> fields = Collections.emptyMap();
    public Map <String, DataValue> properties = Collections.emptyMap();
    
    @Override
    public String toString()
    {
        if (fields.isEmpty() && properties.isEmpty())
            return "";
        
        StringBuilder message = new StringBuilder();
        
        message.append("FIELDS\n");
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            message.append(entry.getValue()).append("\n");
        }
        
        message.append("\nPROPERTIES\n");
        for (Map.Entry <String, DataValue> entry : properties.entrySet())
        {
            message.append(entry.getValue()).append("\n");
        }
        
        return message.toString();
    }
}
