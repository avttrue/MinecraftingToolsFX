package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.Sanction;
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
 * Хэндлер запроса информации об игроке: санкции
 */
public class UserWhoIsSanctionHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        logger.log(Level.INFO, String.format("UserWhoIsSanction.create: %1$d", objectId));
        
        long date = 0;
        long cancel_date = 0;
        long expiration_date = 0;
        String reason = "";
        String cancel_reason = "";
        String sanction = "";
        int moderator = -1;
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            
            if (entry.getKey().equals("date"))
                date = entry.getValue().asLong();
            
            else if (entry.getKey().equals("reason"))
                reason = entry.getValue().asString();
            
            else if (entry.getKey().equals("sanction"))
                sanction = entry.getValue().asString();
        }
        
        for (Map.Entry <String, DataValue> entry : properties.entrySet())
        {
            //System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            
            if (entry.getKey().equals("cancel_date"))
                cancel_date = entry.getValue().asLong();
            
            if (entry.getKey().equals("expiration_date"))
                expiration_date = entry.getValue().asLong();
            
            else if (entry.getKey().equals("cancel_reason"))
                cancel_reason = entry.getValue().asString();
            
            else if (entry.getKey().equals("moderator"))
                moderator = entry.getValue().asInt();
        }
        
        final Sanction finalSanction =
                new Sanction(date, cancel_date, expiration_date, moderator, reason, cancel_reason, sanction);
        
        Runnable SendDataToApplication = () -> {
            connection.packetCount++;
            minecraftingProfiles.SanctionsBuffer.add(finalSanction.clone());
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        
        logger.log(Level.INFO, String.format("UserWhoIsSanction.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("UserWhoIsSanction.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}