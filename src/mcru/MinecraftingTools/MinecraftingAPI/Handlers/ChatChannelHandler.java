package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Helpers.ChannelListElement;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер канала - создание и вся информация
 */
public class ChatChannelHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient client, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        String name = "нет данных";
        String channelType = "нет данных";
        
        // fields
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getKey().equals("channelType"))
                channelType = entry.getValue().asString();
            
            if (entry.getKey().equals("name"))
                name = entry.getValue().asString();
        }
        
        logger.log(Level.INFO, String.format("ChatChannel.create: \"%1$s\", id=%2$d", name, objectId));
        
        //  properties
        //for (Map.Entry<String, DataValue> entry : properties.entrySet())
        //	System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
        final String finalChannelType = channelType;
        final String finalName = name;
        
        Runnable SendDataToApplication = () -> {
            ChannelListElement cle = new ChannelListElement(objectId, finalChannelType, finalName);
            cle.properties = properties;
            cle.fields = fields;
            scene.addChannelToList(cle);
            scene.updateCLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("ChatChannel.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> {
            scene.getChannelListElementByID(objectId).properties = properties;
            scene.updateCLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient client, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("ChatChannel.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> {
            scene.removeChannelFromList(objectId);
            scene.updateCLE();
            connection.packetCount++;
        };
        Platform.runLater(SendDataToApplication);
    }
}
