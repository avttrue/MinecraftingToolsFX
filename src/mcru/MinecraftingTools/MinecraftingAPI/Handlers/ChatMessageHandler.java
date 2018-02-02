package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.MinecraftingAPI.MC_Message;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер служит для отправления сообщения в чат/ЛС
 */
public class ChatMessageHandler implements UnpackedObjectTypeHandler
{
    private static final String SOURCE_PLUGIN = "plugin";
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient client, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("ChatMessage.create: %1$d", objectId));
        
        long timestamp = 0;
        String source = "";
        String message = "";
        long opponent_id = -1;
        long channel_id = -1;
        long author_id = -1;
        long associated_user = -1;
        String date;
        String time;
        
        // fields
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            
            if (entry.getKey().equals("time"))
                timestamp = entry.getValue().asLong();
            
            else if (entry.getKey().equals("source"))
                source = entry.getValue().asString();
            
            else if (entry.getKey().equals("message"))
                message = entry.getValue().asString();
        }
        
        // properties
        for (Map.Entry <String, DataValue> entry : properties.entrySet())
        {
            //System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            
            if (entry.getKey().equals("author"))
                author_id = entry.getValue().asLong();
            
            else if (entry.getKey().equals("channel"))
                channel_id = entry.getValue().asLong();
            
            else if (entry.getKey().equals("associated_user"))
                associated_user = entry.getValue().asLong();
            
            else if (entry.getKey().equals("opponent"))
                opponent_id = entry.getValue().asLong();
        }
        
        if (timestamp <= 0)
        {
            date = TextFunc.MessageDateToString(new Date().getTime());
            time = TextFunc.MessageTimeToString(new Date().getTime());
        }
        else
        {
            date = TextFunc.GetServerMessageDate(timestamp);
            time = TextFunc.GetServerMessageTime(timestamp);
        }
        
        MC_Message mcMessage = new MC_Message();
        mcMessage.source = source;
        mcMessage.author_id = author_id;
        mcMessage.channel_id = channel_id;
        mcMessage.opponent_id = opponent_id;
        mcMessage.date = date;
        mcMessage.time = time;
        mcMessage.id = objectId;
        mcMessage.message = message;
        mcMessage.associated_user = associated_user;
        mcMessage.fields = fields;
        mcMessage.properties = properties;
        mcMessage.type = MC_Message.SIMPLE_MESSAGE;
        // если канал не указан, значит - ЛС
        if (channel_id < 0)
            mcMessage.type = MC_Message.CHAT_MESSAGE;
        // если сообщение он плагина (важнее предыдущей проверки)
        if (source.equals(SOURCE_PLUGIN))
            mcMessage.type = MC_Message.FROM_PLUGIN;
        
        Runnable SendDataToApplication = () -> {
            scene.addMessageToChat(mcMessage);
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient client, ObjectType objectType, long objectId, Map <String, DataValue> changedProperties,
            Map <Integer, DataValue> packedChangedProperties)
    {
        logger.log(Level.INFO, String.format("ChatMessage.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient client, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("ChatMessage.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}
