package mcru.MinecraftingTools;

import mcru.MinecraftingTools.Helpers.StatusListener;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.MinecraftingAPI.MC_Connection;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfiles;
import mcru.MinecraftingTools.MojangAPI.MojangProfiles;

/**
 * Управление и параметры
 */
public final class ApplicationControl
{
    
    // название
    public static final String NAME = "Minecrafting Tools";
    // версия
    public static final String VERSION = "2.0.8";
    // УРЛы и т.п.
    public static final String MinecraftingToolsURL = "https://yadi.sk/d/VDD9fnc43H6gAw";
    public static final String MinecraftingURL = "http://www.minecrafting.ru/";
    //путь до каталога запуска
    public static String MyWorkingDir;
    // путь до файла с настройками
    public static String ConfigFilePath;
    // каталог логов
    public static String LogDirectory;
    // файл логов чата
    public static String LogChatFile;
    // файл логов приложения
    public static String LogApplicationFile;
    // конфиг
    public static Config config;
    // текстова заглушка для "не найдено, не задано"
    public static String notFound = "- - -";
    // текущее соединение с сервером
    public static MC_Connection connection;
    // текущая аутентификация пользователя
    public static AuthentificationData currentAuthData;
    // поток обновления статуса порта
    public static StatusChecker statusChecker;
    // файл аутентификаций
    public static String AuthentificationPath;
    // путь до файла для бэкапов профилей Mojang
    public static String PathMojangProfiles = "";
    // путь до файла для бэкапов профилей Minecrafting
    public static String PathMinecraftingProfiles = "";
    // хранилище бэкапов профилей Mojang
    public static MojangProfiles mojangProfiles = new MojangProfiles();
    // хранилище бэкапов профилей Minecrafting
    public static MinecraftingProfiles minecraftingProfiles = new MinecraftingProfiles();
    // флаг нажатой кнопки CTRL
    public static boolean CTRLisPressed = false;
    // способ сортировки каналов
    public static int channelsListSorting = -1;
    // способ сортировки игроков
    public static int playersListSorting = -1;
    // каталог шаблонов сообщений
    public static String PresetsDirectory = "";
    // счётчик попыток подключений после разрыва связи (-1 - разорвали вручную или сервер не принял)
    public static int countTryConnections = 0;
    // слушатель статусов связи и процессов
    public static StatusListener statusListener = new StatusListener();
    // статусы процессов (для отображения MainScene.progressBar)
    private static boolean statusUserWhoIs = false;
    private static boolean statusUserFind = false;
    private static boolean statusMojangProfileWebSearcher = false;
    private static boolean statusMojangServerStatusChecker = false;
    private static boolean statusConnectToServer = false;
    
    /**
     * Нужно лу отображать {@link MainScene#progressBar}
     */
    public static boolean isShowProgerssBar()
    {
        return statusConnectToServer || statusUserWhoIs || statusMojangProfileWebSearcher || statusUserFind ||
               statusMojangServerStatusChecker;
    }
    
    /**
     * получить statusUserWhoIs
     */
    public static boolean getStatusUserWhoIs()
    {
        return statusUserWhoIs;
    }
    
    /**
     * установить statusUserWhoIs
     */
    public static void setStatusUserWhoIs(boolean value)
    {
        statusUserWhoIs = value;
        statusListener.setShowProgressBar(isShowProgerssBar());
    }
    
    /**
     * получить statusUserFind
     */
    public static boolean getStatusUserFind()
    {
        return statusUserFind;
    }
    
    /**
     * установить statusUserFind
     */
    public static void setStatusUserFind(boolean value)
    {
        statusUserFind = value;
        statusListener.setShowProgressBar(isShowProgerssBar());
    }
    
    /**
     * получить statusMojangProfileWebSearcher
     */
    public static boolean getStatusMojangProfileWebSearcher()
    {
        return statusMojangProfileWebSearcher;
    }
    
    /**
     * установить statusMojangProfileWebSearcher
     */
    public static void setStatusMojangProfileWebSearcher(boolean value)
    {
        statusMojangProfileWebSearcher = value;
        statusListener.setShowProgressBar(isShowProgerssBar());
    }
    
    /**
     * получить statusMojangServerStatusChecker
     */
    public static boolean getStatusMojangServerStatusChecker()
    {
        return statusMojangServerStatusChecker;
    }
    
    /**
     * установить statusMojangServerStatusChecker
     */
    public static void setStatusMojangServerStatusChecker(boolean value)
    {
        statusMojangServerStatusChecker = value;
        statusListener.setShowProgressBar(isShowProgerssBar());
    }
    
    /**
     * получить statusConnectToServer
     */
    public static boolean getStatusConnectToServer()
    {
        return statusConnectToServer;
    }
    
    /**
     * установить statusConnectToServer
     */
    public static void setStatusConnectToServer(boolean value)
    {
        statusConnectToServer = value;
        statusListener.setShowProgressBar(isShowProgerssBar());
    }
}
