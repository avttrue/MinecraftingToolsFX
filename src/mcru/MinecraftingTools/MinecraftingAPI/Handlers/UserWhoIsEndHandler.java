package mcru.MinecraftingTools.MinecraftingAPI.Handlers;

import javafx.application.Platform;
import mcru.MinecraftingTools.Helpers.ContentElement;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Helpers.MyLink;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfile;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.env.ObjectType;
import ontando.minecrafting.remote_access.env.UnpackedObjectTypeHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Хэндлер запроса информации об игроке: окончание
 */
public class UserWhoIsEndHandler implements UnpackedObjectTypeHandler
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    @Override
    public void create(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> fields,
            DataValue[] packedFields, Map <String, DataValue> properties, Map <Integer, DataValue> packedProperties)
    {
        int user = -1;
        
        logger.log(Level.INFO, String.format("UserWhoIsEnd.create: %1$d", objectId));
        
        for (Map.Entry <String, DataValue> entry : fields.entrySet())
        {
            //System.out.println("Field.Key: " + entry.getKey() + ", Value: " + entry.getValue());
            
            if (entry.getKey().equals("user"))
                user = entry.getValue().asInt();
        }

//        for (Map.Entry<String, DataValue> entry : properties.entrySet())
//            System.out.println("Property.Key: " + entry.getKey() + ", Value: " + entry.getValue());
        
        final long user_final = user;
        
        Runnable SendDataToApplication = () -> {
            connection.packetCount++;
            PlayerListElement ple = scene.getPlayerListElementByID(user_final);
            String colorBG = null;
            
            MinecraftingProfile mp = new MinecraftingProfile();
            mp.UUID = ple.uuid;
            mp.Nick = ple.nick;
            mp.LongDateTime = new Date().getTime();
            minecraftingProfiles.addUseBuffer(mp);
            
            ArrayList <ContentElement> message = new ArrayList <>();
            message.add(new ContentElement("Получены данные об игроке "));
            message.add(new ContentElement(ple.nick, new MyLink("playerID", String.valueOf(user_final)).get())
                                .addElementClass("success-message"));
            message.add(new ContentElement(String.format(" (%1$s) с сервера Minecrafting.ru", ple.uuid)));
            
            scene.logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_SUCCESS));
            scene.updatePLE();
            setStatusUserWhoIs(false);
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void update(
            LocalClient localClient, ObjectType objectType, long objectId, Map <String, DataValue> properties,
            Map <Integer, DataValue> packedProperties)
    {
        
        logger.log(Level.INFO, String.format("UserWhoIsEnd.update: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public void destroy(LocalClient localClient, ObjectType objectType, long objectId)
    {
        logger.log(Level.INFO, String.format("UserWhoIsEnd.destroy: %1$d", objectId));
        
        Runnable SendDataToApplication = () -> connection.packetCount++;
        Platform.runLater(SendDataToApplication);
    }
}