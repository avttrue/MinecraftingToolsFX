package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер для получения информации по игроку
 */
public class UserHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        String uuid = "";
        String nick = "";
        int online_flags = 0;
        int status_flags = 0;
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getKey().equals("uuid"))
                uuid = entry.getValue().asString();
            
            else if (entry.getKey().equals("nick"))
                nick = entry.getValue().asString();
        }
        
        for (Map.Entry <String, DataValue> entry : properties.entrySet())
        {
            //System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getKey().equals("online_flags"))
                online_flags = entry.getValue().asInt();
            
            else if (entry.getKey().equals("status_flags"))
                status_flags = entry.getValue().asInt();
            
        }
        
        logger.log(Level.INFO, String.format("User.create: \"%1$s\", id=%2$d", nick, objectId));
        
        final String finalNick = nick;
        final String finalUuid = uuid;
        final int finalOnline_flags = online_flags;
        final int finalStatus_flags = status_flags;
        
        Runnable SendDataToApplication = () -> {
            PlayerListElement ple = new PlayerListElement(objectId, finalNick, finalUuid);
            ple.online_flags = finalOnline_flags;
            ple.status_flags = finalStatus_flags;
            ple.setFields(fields);
            ple.setProperties(properties);
            
            // флаг того, что игрок меньше указанного времени на сервере
            for (Map.Entry <String, DataValue> entry : ple.getProperties().entrySet())
            {
                if (entry.getKey().equals("played_server"))
                {
                    ple.alert = (entry.getValue().asLong() <= config.PlayedServerAlertValue * 3600);
                    break;
                }
            }
            
            scene.addPlayerToList(ple);
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
        
        logger.log(Level.INFO, String.format("User.update: %1$d", objectId));
        
        int online_flags = 0;
        int status_flags = 0;
        
        for (Map.Entry <String, DataValue> entry : properties.entrySet())
        {
            if (entry.getKey().equals("online_flags"))
                online_flags = entry.getValue().asInt();
            
            else if (entry.getKey().equals("status_flags"))
                status_flags = entry.getValue().asInt();
            
        }
        
        final int finalOnline_flags = online_flags;
        final int finalStatus_flags = status_flags;
        
        Runnable SendDataToApplication = () -> {
            PlayerListElement ple = scene.getPlayerListElementByID(objectId);
            
            ple.online_flags = finalOnline_flags;
            ple.status_flags = finalStatus_flags;
            ple.setProperties(properties);
            
            // флаг того, что игрок меньше указанного времени на сервере
            for (Map.Entry <String, DataValue> entry : ple.getProperties().entrySet())
            {
                if (entry.getKey().equals("played_server"))
                {
                    ple.alert = (entry.getValue().asLong() <= config.PlayedServerAlertValue * 3600);
                    break;
                }
            }
            
            scene.updatePLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("User.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> {
            scene.removePlayerFromList(objectId);
            scene.updatePLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
}
