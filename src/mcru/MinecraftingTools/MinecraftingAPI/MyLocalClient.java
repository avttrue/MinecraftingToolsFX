package mcru.MinecraftingTools.MinecraftingAPI;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import mcru.MinecraftingTools.Functions.JsonFunc;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Interface.ContentPane;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationList;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.ServerAuthInformation;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.ClientInformation;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.client.RegistrationWaiter;
import ontando.minecrafting.remote_access.network.PacketSender;
import ontando.minecrafting.remote_access.network.packet.PacketManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.MyApplication.scene;

public class MyLocalClient extends LocalClient
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    public MyLocalClient(
            PacketManager packetManager, Logger logger, PacketSender remote, ClientInformation information,
            RegistrationWaiter waiter)
    {
        
        super(packetManager, logger, remote, information, waiter);
    }
    
    @Override
    public final void logDisconnect()
    {
        super.logDisconnect();
        
        logger.log(Level.INFO, "Соединение с сервером разорвано");
        
        Runnable SendDataToApplication = () -> {
            scene.clearConnection();
            connection.packetCount = 0;
            
            for (Tab tab : scene.tabpaneContent.getTabs())
            {
                ContentPane cp = (ContentPane) tab.getContent();
                if (cp.modeWebNavigation)
                    continue;
                cp.addSeparator("Соединение с сервером разорвано");
            }
            
            // восстановление связи
            if (config.TryToReconnected && countTryConnections > -1 &&
                countTryConnections <= config.CountTryReconnection)
            {
                // увеличиваю счётчик попыток подключения
                countTryConnections++;
                scene.logContent.addMessage(new LogMessage(String.format("Пытаемся восстановить связь, попытка %d...",
                                                                         countTryConnections),
                                                           LogMessage.MESSAGE_INFO));
                scene.startAuthentification();
            }
            
        };
        Platform.runLater(SendDataToApplication);
    }
    
    @Override
    public final void logError(Exception e)
    {
        super.logError(e);
        
        logger.log(Level.SEVERE, "Ошибка", e);
    }
    
    @Override
    public final void authComplete(int statusCode, String session, @Nullable byte[] userToken, @Nullable String message)
    {
        super.authComplete(statusCode, session, userToken, message);
        
        logger.log(Level.INFO, "Аутентификация завершена: " + session);
        
        Runnable SendDataToApplication = () -> {
            if (currentAuthData.getServerAuthInformation() != null)
            {
                currentAuthData.getServerAuthInformation().Session = session;
                
                if (userToken != null)
                    currentAuthData.getServerAuthInformation().UserToken = userToken;
            }
            else
                currentAuthData.setServerAuthInformation(new ServerAuthInformation(userToken, session));
            
            if (statusCode > 0)
            {
                // отключаем попытки восстановить связь
                countTryConnections = -1;
                scene.logContent.addMessage(new LogMessage(String.format("Сервер не принял вас (%1$d)", statusCode),
                                                           LogMessage.MESSAGE_ERROR));
                scene.logContent.addMessage(new LogMessage("Если проблемы с подключением к серверу не прекратятся, " +
                                                           "удалите аутентификационные данные и пройдите аутентификацию заново",
                                                           LogMessage.MESSAGE_ERROR));
            }
            else if (statusCode == 0)
            {
                scene.logContent.addMessage(new LogMessage("Сервер принял вас!", LogMessage.MESSAGE_SUCCESS));
                
                // обнуляю счётчик попыток восстановления связи
                countTryConnections = 0;
                
                // записываю новые аутентификационные данные в файл настроек аутентификации
                AuthentificationList authentificationList = new AuthentificationList();
                
                // если уже есть файл - загружаю
                File testFile = new File(AuthentificationPath);
                if (testFile.exists() && testFile.isFile())
                {
                    Object o = JsonFunc.loadObjectFormFile(AuthentificationPath, authentificationList);
                    
                    if (o instanceof AuthentificationList)
                        authentificationList = (AuthentificationList) o;
                    else
                        logger.log(Level.SEVERE, "Некорректно прочитан файл аутентификаций, будет переписан");
                }
                
                AuthentificationData ad = new AuthentificationData(currentAuthData);
                
                if (!config.RememberPassword)
                    ad.setPassword("");
                
                authentificationList.addAuthentification(ad);
                
                // сохраняю
                if (!JsonFunc.saveObjectToFile(AuthentificationPath, authentificationList))
                    logger.log(Level.SEVERE, "Ошибка при сохранении файла аутентификаций");
            }
        };
        
        Platform.runLater(SendDataToApplication);
    }
}
