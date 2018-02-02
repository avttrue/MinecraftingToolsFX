//createObjectTypesDump() - создает неизменяемый спиок всех типов объектов, которые могут к тебе приходить. (С легким доступом к содержимому)
// createControlChannelsDump() - создает неизменяемый список всего того, что ты можешь отправлять на сервер.  (С легким доступом к содержимому)
//Учитывая, что и те и другие данные шлет тебе сервер, надо вызывть их после подключения (Наверное, неплохо сделать кнопочку по которой эти данные грузятся).

package mcru.MinecraftingTools.MinecraftingAPI;

import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.ServerAuthInformation;
import mcru.MinecraftingTools.MinecraftingAPI.Handlers.*;
import mcru.MinecraftingTools.MyApplication;
import ontando.minecrafting.remote_access.client.ClientCreator;
import ontando.minecrafting.remote_access.client.ClientInformation;
import ontando.minecrafting.remote_access.client.LocalClient;
import ontando.minecrafting.remote_access.client.ext.config.ClientConfigHandler;
import ontando.minecrafting.remote_access.client.ext.config.ExtensionConfig;
import ontando.minecrafting.remote_access.client.java_websocket.TestWebSocketClient;
import ontando.minecrafting.remote_access.env.*;
import ontando.minecrafting.remote_access.network.PacketSender;
import ontando.minecrafting.remote_access.network.io.Util;
import ontando.minecrafting.remote_access.network.packet.PacketManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Библиотека Ангала
 * https://bitbucket.org/minecrafting/remoteaccess/commits/all
 * files.ontando.ru/jars/javaClient-with-deps.jar
 */
public class MC_Connection implements ClientCreator
{
    public final ClientInformation information;
    private final String clientToken;
    public long packetCount = 0; // информация просто для понтов
    public String address;
    public int port;
    public String login;
    public MyLocalClient client;
    public ClientEnvironmentManager clientEnvironmentManager;
    public int chatMessageID = -1;
    public int partyInviteID = -1;
    public int partyLeaveID = -1;
    public int userWhoisID = -1;
    public int userFindID = -1;
    private int userFindEndID = -1;
    private int userFindEntryID = -1;
    private int userWhoisIpID = -1;
    private int userWhoisSanctionID = -1;
    private int userWhoisEndID = -1;
    private int chatChannelID = -1;
    private int chatJoinID = -1;
    private int chatPartID = -1;
    private int userID = -1;
    private int userJoinID = -1;
    private int userQuitID = -1;
    private PacketManager packetManager;
    private TestWebSocketClient webSocketClient;
    private ClientConfigHandler config;
    private Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    public MC_Connection(
            String address, int port, String login, String password, ServerAuthInformation lastSAI, String clientToken)
    {
        // Идентификатор клиента - должен быть единым для всего приложения и не меняться.
        this.clientToken = clientToken;
        // Информация клиента на каком-то сервере. Описание содержимого в классе.
        this.information = new ClientInformation(this.clientToken);
        this.information.password = password;
        this.address = address;
        this.port = port;
        this.login = login;
        
        if (lastSAI != null)
        {
            information.userToken = lastSAI.UserToken;
            information.session = lastSAI.Session;
        }
        userJoinID = -1;
    }
    
    public final void Disconnect()
    {
        logger.log(Level.INFO, "Получена команда разорвать соединение с сервером");
        this.packetCount = 0;
        try
        {
            if (this.webSocketClient != null && this.webSocketClient.getPingThread() != null)
            {
                this.webSocketClient.getPingThread().stop();
                logger.log(Level.INFO, "webSocketClient: PingThread остановлен");
            }
            
            if (this.webSocketClient != null)
            {
                this.webSocketClient.close();
                logger.log(Level.INFO, "webSocketClient: соединение разорвано");
            }
            
            if (this.client != null)
            {
                this.client.disconnect();
                logger.log(Level.INFO, "client: соединение разорвано");
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка при разрыве связи", e);
        }
    }
    
    public final int start() throws URISyntaxException, InterruptedException
    {
        int result = -1;
        URI uri;
        this.packetCount = 0;
        
        logger.log(Level.INFO, String.format("Получен clientToken: %s", this.clientToken));
        
        // Если ClientInformation#UserToken нам не известен, регистрируемся, что бы получить новый.
        if (this.information.userToken == null)
        {
            logger.log(Level.INFO, "Аутентификация требуется");
            uri = Util.registerUri(address, port, this.clientToken, login);
            result = 1;
        }
        else
        {
            logger.log(Level.INFO, String.format("Аутентификация не требуется: %s", information.session));
            uri = Util.loginUri(address, port, this.clientToken, this.information.userToken);
            result = 0;
        }
        
        this.clientEnvironmentManager = new ClientEnvironmentManager();
        
        // Обработчик конфигов (и расширений сервер).
        this.config = new ClientConfigHandler(this.clientEnvironmentManager);
        
        ChatMessageHandler chatMessageHandler = new ChatMessageHandler();
        ChatChannelHandler chatChannelHandler = new ChatChannelHandler();
        ChatJoinHandler chatJoinHandler = new ChatJoinHandler();
        ChatPartHandler chatPartHandler = new ChatPartHandler();
        PartyInviteHandler partyInviteHandler = new PartyInviteHandler();
        PartyLeaveHandler partyLeaveHandler = new PartyLeaveHandler();
        UserHandler userHandler = new UserHandler();
        UserJoinHandler userJoinHandler = new UserJoinHandler();
        UserQuitHandler userQuitHandler = new UserQuitHandler();
        UserWhoIsHandler userWhoIsHandler = new UserWhoIsHandler();
        UserWhoIsEndHandler userWhoIsEndHandler = new UserWhoIsEndHandler();
        UserWhoIsIpHandler userWhoIsIpHandler = new UserWhoIsIpHandler();
        UserWhoIsSanctionHandler userWhoIsSanctionHandler = new UserWhoIsSanctionHandler();
        UserFindHandler userFindHandler = new UserFindHandler();
        UserFindEndHandler userFindEndHandler = new UserFindEndHandler();
        UserFindEntryHandler userFindEntryHandler = new UserFindEntryHandler();
        
        this.clientEnvironmentManager.registerTypeHandler("chat.message", chatMessageHandler);
        this.clientEnvironmentManager.registerTypeHandler("chat.channel", chatChannelHandler);
        this.clientEnvironmentManager.registerTypeHandler("chat.join", chatJoinHandler);
        this.clientEnvironmentManager.registerTypeHandler("chat.part", chatPartHandler);
        this.clientEnvironmentManager.registerTypeHandler("party.invite", partyInviteHandler);
        this.clientEnvironmentManager.registerTypeHandler("party.leave", partyLeaveHandler);
        this.clientEnvironmentManager.registerTypeHandler("user", userHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.join", userJoinHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.quit", userQuitHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.whois", userWhoIsHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.whois.ip", userWhoIsIpHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.whois.sanction", userWhoIsSanctionHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.whois.end", userWhoIsEndHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.find", userFindHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.find.end", userFindEndHandler);
        this.clientEnvironmentManager.registerTypeHandler("user.find.entry", userFindEntryHandler);
        
        this.config.registerInEnvironment();
        
        // Устанавливае действия при успешной загрузке окружения и информации
        this.config.onLoadComplete(this::postLoadActions);
        
        this.packetManager = PacketManager.clientHandlers(clientEnvironmentManager);
        
        this.webSocketClient = new TestWebSocketClient(uri, this.packetManager, logger, this);
        
        logger.log(Level.INFO,
                   String.format(
                           "Пытаемся подключиться к серверу, адрес: %1$s, порт: %2$d, логин: %3$s, пароль: %4$s, токен: %5$s",
                           address,
                           port,
                           login,
                           information.password,
                           clientToken));
        
        // Запускаем клиент..
        if (this.webSocketClient.connectBlocking())
        {
            logger.log(Level.INFO, "Успешно");
        }
        else
        {
            
            logger.log(Level.SEVERE, "Неудачно");
            result = -1;
        }
        
        return result;
    }
    
    @Override
    public final LocalClient create(PacketSender packetSender)
    {
        MyLocalClient lc = new MyLocalClient(this.packetManager, logger, packetSender, this.information, () -> {
        });
        
        this.client = lc;
        return lc;
    }
    
    private void postLoadActions()
    {
        chatMessageID = this.clientEnvironmentManager.getChannelId("chat.message");
        chatChannelID = this.clientEnvironmentManager.getChannelId("chat.channel");
        chatJoinID = this.clientEnvironmentManager.getChannelId("chat.join");
        chatPartID = this.clientEnvironmentManager.getChannelId("chat.part");
        partyInviteID = this.clientEnvironmentManager.getChannelId("party.invite");
        partyLeaveID = this.clientEnvironmentManager.getChannelId("party.leave");
        userID = this.clientEnvironmentManager.getChannelId("user");
        userJoinID = this.clientEnvironmentManager.getChannelId("user.join");
        userQuitID = this.clientEnvironmentManager.getChannelId("user.quit");
        userWhoisID = this.clientEnvironmentManager.getChannelId("user.whois");
        userWhoisSanctionID = this.clientEnvironmentManager.getChannelId("user.whois.sanction");
        userWhoisIpID = this.clientEnvironmentManager.getChannelId("user.whois.ip");
        userWhoisEndID = this.clientEnvironmentManager.getChannelId("user.whois.end");
        userFindID = this.clientEnvironmentManager.getChannelId("user.find");
        userFindEndID = this.clientEnvironmentManager.getChannelId("user.find.end");
        userFindEntryID = this.clientEnvironmentManager.getChannelId("user.find.entry");
        
        logger.log(Level.INFO, "chatMessageID: " + String.valueOf(chatMessageID));
        logger.log(Level.INFO, "chatChannelID: " + String.valueOf(chatChannelID));
        logger.log(Level.INFO, "chatJoinID: " + String.valueOf(chatJoinID));
        logger.log(Level.INFO, "chatPartID: " + String.valueOf(chatPartID));
        logger.log(Level.INFO, "partyInviteID: " + String.valueOf(partyInviteID));
        logger.log(Level.INFO, "partyLeaveID: " + String.valueOf(partyLeaveID));
        logger.log(Level.INFO, "userID: " + String.valueOf(userID));
        logger.log(Level.INFO, "userJoinID: " + String.valueOf(userJoinID));
        logger.log(Level.INFO, "userQuitID: " + String.valueOf(userQuitID));
        logger.log(Level.INFO, "userWhoisID: " + String.valueOf(userWhoisID));
        logger.log(Level.INFO, "userWhoisIpID: " + String.valueOf(userWhoisIpID));
        logger.log(Level.INFO, "userWhoisSanctionID: " + String.valueOf(userWhoisSanctionID));
        logger.log(Level.INFO, "userWhoisEndID: " + String.valueOf(userWhoisEndID));
        logger.log(Level.INFO, "userFindID: " + String.valueOf(userFindID));
        logger.log(Level.INFO, "userFindEndID: " + String.valueOf(userFindEndID));
        logger.log(Level.INFO, "userFindEntryID: " + String.valueOf(userFindEntryID));
    }
    
    /**
     * Вывод структуры протокола
     */
    public final String dumpProtocol()
    {
        StringBuilder result = new StringBuilder();
        try
        {
            KnownObjectTypes objects = this.clientEnvironmentManager.createObjectTypesDump();
            Set <String> remainingObjects = new HashSet <>(objects.byName.keySet());
            KnownControlChannels channels = this.clientEnvironmentManager.createControlChannelsDump();
            Set <String> remainingChannels = new HashSet <>(channels.map.keySet());
            
            for (String name : this.config.listExtensions())
            {
                ExtensionConfig config = this.config.getConfig(name);
                result.append(String.format("Extension: %s, ver: %s, Protocol: %d\n",
                                            config.getName(),
                                            config.getVersion(),
                                            config.getProtocolVersionNumber()));
                result.append(this.getSubDump(name, objects, remainingObjects, channels, remainingChannels));
                
                config.requestDescription(extensionConfig -> {
                    StringBuilder ChannelsDescription = new StringBuilder();
                    ChannelsDescription.append(String.format("ChannelsDescription (%s):{\n", name));
                    Map <String, List <String>> map = extensionConfig.getChannelsDescription();
                    for (Map.Entry <String, List <String>> entry : map.entrySet())
                    {
                        ChannelsDescription.append(String.format("\t%s:\n", entry.getKey()));
                        for (String s : entry.getValue())
                        {
                            ChannelsDescription.append(String.format("\t\t%s\n", s));
                        }
                    }
                    ChannelsDescription.append("}");
                    System.out.println(ChannelsDescription.toString());
                });
                config.requestDescription(extensionConfig -> {
                    StringBuilder ObjectsDescription = new StringBuilder();
                    ObjectsDescription.append(String.format("ObjectsDescription (%s):{\n", name));
                    Map <String, List <String>> map = extensionConfig.getObjectsDescription();
                    for (Map.Entry <String, List <String>> entry : map.entrySet())
                    {
                        ObjectsDescription.append(String.format("\t%s:\n", entry.getKey()));
                        for (String s : entry.getValue())
                        {
                            ObjectsDescription.append(String.format("\t\t%s\n", s));
                        }
                    }
                    ObjectsDescription.append("}\n");
                    System.out.println(ObjectsDescription.toString());
                });
                
                result.append(TextFunc.GetSpace(10, '-')).append("\n");
            }
            result.append("Extension: <unknown>\n");
            this.getSubDump("", objects, remainingObjects, channels, remainingChannels);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка", e);
        }
        return result.toString();
    }
    
    /**
     * Вывод дампа протокола, используется в {@link MC_Connection#dumpProtocol}
     */
    private String getSubDump(
            String extName, KnownObjectTypes objects, Set <String> remainingObjects, KnownControlChannels channels,
            Set <String> remainingChannels)
    {
        StringBuilder result = new StringBuilder();
        try
        {
            result.append("* Objects:\n");
            Iterator <String> iterator = remainingObjects.iterator();
            while (iterator.hasNext())
            {
                String objectName = iterator.next();
                if (!objectName.startsWith(extName))
                {
                    continue;
                }
                iterator.remove();
                ImmutableObjectType type = objects.byName.get(objectName);
                result.append(String.format(" * %s\n", type.name));
                result.append(String.format(" * Instant: %s\n", type.instant));
                if (!type.fields.isEmpty())
                {
                    result.append("  * Fields: \n");
                    for (DataEntry entry : type.fields.values())
                    {
                        result.append(String.format("   * %s: %s\n", entry.name, entry.type.id));
                    }
                }
                if (!type.properties.isEmpty())
                {
                    result.append("  * Properties: (Optional)\n");
                    for (DataEntry entry : type.properties.values())
                    {
                        result.append(String.format("   * %s: %s\n", entry.name, entry.type.id));
                    }
                }
            }
            result.append("* Channels:\n");
            Iterator <String> channelIterator = remainingChannels.iterator();
            while (channelIterator.hasNext())
            {
                String channelName = channelIterator.next();
                if (!channelName.startsWith(extName))
                {
                    continue;
                }
                channelIterator.remove();
                ImmutableControlChannel type = channels.map.get(channelName);
                result.append(String.format(" * %s\n", type.name));
                result.append(String.format(" * Session id: %s (Changes each time)\n", type.channelId));
                if (!type.params.isEmpty())
                {
                    result.append("  * Parameters:\n");
                    for (DataEntry entry : type.params)
                    {
                        result.append(String.format("   * %s: %s\n", entry.name, entry.type.id));
                    }
                }
                if (!type.options.isEmpty())
                {
                    result.append("  * Options: (Optional)\n");
                    for (DataEntry entry : type.options.values())
                    {
                        result.append(String.format("   * %s: %s\n", entry.name, entry.type.id));
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка", e);
        }
        
        return result.toString();
    }
}
