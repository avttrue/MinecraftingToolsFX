package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.ChannelListElement;
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

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер присоединения к каналу пользователя
 */
public class ChatJoinHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("ChatJoin.create: %1$d", objectId));
        
        long player_id = -1;
        long channel_id = -1;
        long timestamp = 0;
        String date;
        String time;
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getKey().equals("user"))
                player_id = entry.getValue().asInt();
            
            else if (entry.getKey().equals("channel"))
                channel_id = entry.getValue().asInt();
            
            else if (entry.getKey().equals("time"))
                timestamp = entry.getValue().asLong();
        }
        
        //for (Map.Entry<String, DataValue> entry : properties.entrySet())
        //	System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
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
        final long finalPlayer_id = player_id;
        final long finalChannel_id = channel_id;
        final String finalTime = time;
        final String finalDate = date;
        
        Runnable SendDataToApplication = () -> {
            
            connection.packetCount++;
            
            if (finalPlayer_id == -1 || finalChannel_id == -1)
                return;
            
            ChannelListElement cle = scene.getChannelListElementByID(finalChannel_id);
            cle.AddPlayerToList(finalPlayer_id);
            scene.updateCLE();
            
            // пишем в чат
            MC_Message mcMessage = new MC_Message();
            mcMessage.associated_user = finalPlayer_id;
            mcMessage.message = "ПРИСОЕДИНИЛСЯ";
            mcMessage.source = config.SourceIsMinecraftingTools;
            mcMessage.time = finalTime;
            mcMessage.date = finalDate;
            mcMessage.author_id = finalPlayer_id;
            mcMessage.channel_id = finalChannel_id;
            mcMessage.type = MC_Message.INCOMING;
            
            scene.addMessageToChat(mcMessage);
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("ChatJoin.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> {
            scene.updateCLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("ChatJoin.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}
