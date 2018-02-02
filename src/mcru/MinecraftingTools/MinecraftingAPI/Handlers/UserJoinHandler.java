package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
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
 * Хэндлер присоединения игрока к серверу
 */
public class UserJoinHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("UserJoin.create: %1$d", objectId));
        
        int player_id = -1;
        long timestamp = 0;
        String date;
        String time;
        String player_tp = "нет данных";
        String player_sp = "нет данных";
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getKey().equals("user"))
                player_id = entry.getValue().asInt();
            
            else if (entry.getKey().equals("time"))
                timestamp = entry.getValue().asLong();
        }
        //for (Map.Entry<String, DataValue> entry : properties.entrySet())
        //	System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
        if (player_id < 0)
        {
            logger.log(Level.SEVERE, String.format("ID игрока некорректный (%1$d)", player_id));
            return;
        }
        
        PlayerListElement ple = scene.getPlayerListElementByID(player_id);
        
        if (ple != null && ple.getProperties() != null)
        {
            for (Map.Entry <String, DataValue> entry : ple.getProperties().entrySet())
            {
                if (entry.getKey().equals("played_total"))
                    player_tp = TextFunc.TimeIntervalToString(entry.getValue().asLong(), false);
                
                else if (entry.getKey().equals("played_server"))
                    player_sp = TextFunc.TimeIntervalToString(entry.getValue().asLong(), false);
            }
        }
        else
            logger.log(Level.SEVERE, String.format("Игрок не найден в списке (%1$d)", player_id));
        
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
        
        // пишем в чат
        MC_Message mcMessage = new MC_Message();
        mcMessage.associated_user = player_id;
        mcMessage.message = String.format("ЗАШЁЛ: в игре %1$s, на сервере %2$s", player_tp, player_sp);
        mcMessage.source = config.SourceIsMinecraftingTools;
        mcMessage.time = time;
        mcMessage.date = date;
        mcMessage.author_id = player_id;
        mcMessage.type = MC_Message.INFORMATION;
        
        Runnable SendDataToApplication = () -> {
            scene.addMessageToChat(mcMessage);
            scene.updatePLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        
        logger.log(Level.INFO, String.format("UserJoin.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("UserJoin.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}