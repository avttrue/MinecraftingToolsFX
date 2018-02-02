package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Helpers.ContentElement;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Helpers.MyLink;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер поиска игрока на сервере: кого нашли
 */
public class UserFindEntryHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("UserFindEntry.create: %1$d", objectId));
        
        int user = -1;
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
//            System.out.println("UserFindEntry.Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getKey().equals("user"))
                user = entry.getValue().asInt();
        }
//
//        for (Map.Entry<String, DataValue> entry : properties.entrySet())
//            System.out.println("UserFindEntry.Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
        final long user_final = user;
        Runnable SendDataToApplication = () -> {
            connection.packetCount++;
            PlayerListElement ple = scene.getPlayerListElementByID(user_final);
            
            ArrayList <ContentElement> message = new ArrayList <>();
            message.add(new ContentElement("Найден игрок "));
            message.add(new ContentElement(ple.nick, new MyLink("playerID", String.valueOf(user_final)).get())
                                .addElementClass("info-message"));
            message.add(new ContentElement(String.format(" (%1$s) на сервере Minecrafting.ru", ple.uuid)));
            
            scene.logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_INFO));
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        
        logger.log(Level.INFO, String.format("UserFindEntry.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("UserFindEntry.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}