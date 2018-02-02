package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.ApplicationControl.minecraftingProfiles;

/**
 * Хэндлер запроса информации об игроке: IP
 */
public class UserWhoIsIpHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        String ip = "";
        int times = 0;
        
        logger.log(Level.INFO, String.format("UserWhoIsIP.create: %1$d", objectId));
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            
            if (entry.getKey().equals("ip"))
                ip = entry.getValue().asString();
            
            else if (entry.getKey().equals("times"))
                times = entry.getValue().asInt();
        }

//        for (Map.Entry<String, DataValue> entry : properties.entrySet())
//            System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
        final String ip_final = ip;
        final int times_final = times;
        
        Runnable SendDataToApplication = () -> {
            connection.packetCount++;
            minecraftingProfiles.IPBuffer.put(ip_final, times_final);
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        
        logger.log(Level.INFO, String.format("UserWhoIsIP.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("UserWhoIsIP.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}