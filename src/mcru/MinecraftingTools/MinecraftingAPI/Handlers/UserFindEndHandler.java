package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.ApplicationControl.setStatusUserFind;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер поиска игрока на сервере: окончание
 */
public class UserFindEndHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("UserFindEnd.create: %1$d", objectId));

//        for (Map.Entry <String, DataValue> entry : fields.entrySet())
//            System.out.println("UserFindEnd.Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
//
//        for (Map.Entry<String, DataValue> entry : properties.entrySet())
//            System.out.println("UserFindEnd.Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
        Runnable SendDataToApplication = () -> {
            connection.packetCount++;
            
            scene.logContent
                    .addMessage(new LogMessage("Поиск на сервере Minecrafting.ru окончен", LogMessage.MESSAGE_INFO));
            
            setStatusUserFind(false);
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("UserFindEnd.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("UserFindEnd.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}