package mcru.MinecraftingTools;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mcru.MinecraftingTools.Functions.FileFunc;
import mcru.MinecraftingTools.Functions.JsonFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.MyLogFormatter;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfiles;
import mcru.MinecraftingTools.MojangAPI.MojangProfiles;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Dialogs.CommonDialogs.ShowConfirmationDialog;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;

public class MyApplication extends Application
{
    // основная сцена
    public static MainScene scene;
    // сообщения, которые будут переданы в панель событий
    public static StringBuilder messages = new StringBuilder();
    //логгер
    private static Logger LOGGER;
    
    public static void main(String[] args)
    {
        messages.append(String.format("<%4$s STYLE=\"color:#%3$s\">Привет! %1$s %2$s\n</%4$s><BR>",
                                      NAME,
                                      VERSION,
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // определяем ключи приложения
        boolean consoleLoggerON = true;
        boolean fileLoggerON = true;
        boolean useWorkingDir = false;
        boolean useHomeDir = false;
        boolean fileLoggerUnicName = false;
        for (String arg : args)
        {
            if (arg.toLowerCase().equals(("-ConsoleLogOFF").toLowerCase()))
                consoleLoggerON = false;
            else if (arg.toLowerCase().equals(("-FileLogOFF").toLowerCase()))
                fileLoggerON = false;
            else if (arg.toLowerCase().equals(("-FileLogUnicName").toLowerCase()))
                fileLoggerUnicName = true;
            else if (arg.toLowerCase().equals(("-UseWorkingDir").toLowerCase()))
                useWorkingDir = true;
            else if (arg.toLowerCase().equals(("-UseHomeDir").toLowerCase()))
                useHomeDir = true;
        }
        
        messages.append(String.format("<%3$s STYLE=\"color:#%2$s\">Ключи запуска: %1$s\n</%3$s><BR>",
                                      String.join(" ", args),
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // определяем путь до рабочего каталога
        if (useHomeDir && !useWorkingDir)
            MyWorkingDir = System.getProperty("user.home");
        else if (useWorkingDir && !useHomeDir)
            MyWorkingDir = System.getProperty("user.dir");
        else
            MyWorkingDir = FileFunc.getJarDir(MyApplication.class.getProtectionDomain().getCodeSource());
        
        // определяем путь до файла настроек
        ConfigFilePath = MyWorkingDir + File.separator + "config.json";
        
        messages.append(String.format("<%3$s STYLE=\"color:#%2$s\">Файл настроек: %1$s\n</%3$s><BR>",
                                      ConfigFilePath,
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // определяем путь до файла аутентификаций
        AuthentificationPath = MyWorkingDir + File.separator + "authentification.json";
        
        messages.append(String.format("<%3$s STYLE=\"color:#%2$s\">Файл аутентификаций: %1$s\n</%3$s><BR>",
                                      AuthentificationPath,
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // загрузка настроек
        LoadConfig(ConfigFilePath);
        
        // запускам логгер
        CreateLogger(fileLoggerON, consoleLoggerON, fileLoggerUnicName, "UTF-8", config.LoggingLevel);
        
        LOGGER.fine("Привет! " + ApplicationControl.NAME + " v. " + ApplicationControl.VERSION);
        LOGGER.log(Level.INFO,
                   "ОС: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " +
                   System.getProperty("os.arch"));
        LOGGER.log(Level.INFO, "Java: " + System.getProperty("java.runtime.version"));
        
        LOGGER.log(Level.INFO, "Кодировка определена как \"" + System.getProperty("file.encoding") + "\"");
        
        LOGGER.log(Level.INFO, "Рабочий каталог: " + MyWorkingDir);
        
        messages.append(String.format("<%3$s STYLE=\"color:#%2$s\">Рабочий каталог: %1$s\n</%3$s><BR>",
                                      MyWorkingDir,
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // определяем каталог для логов
        LogDirectory = String.format("%1$s%2$sLogs", MyWorkingDir, File.separator);
        File logDir = new File(LogDirectory);
        if (!logDir.exists() || !logDir.isDirectory())
            try
            {
                if (logDir.mkdir())
                    messages.append(String.format(
                            "<%3$s STYLE=\"color:#%2$s\">Создан каталог для логов: %1$s\n</%3$s><BR>",
                            LogDirectory,
                            TextFunc.ColorToRGBCode(Color.DARKBLUE),
                            "SPAN"));
                else
                {
                    LogDirectory = MyWorkingDir;
                    messages.append(String.format(
                            "<%3$s STYLE=\"color:#%2$s\">Каталог для логов создать не удалось, будет использоваться: %1$s\n</%3$s><BR>",
                            LogDirectory,
                            TextFunc.ColorToRGBCode(Color.DARKRED),
                            "SPAN"));
                }
            }
            catch (SecurityException e)
            {
                messages.append(String.format(
                        "<%3$s STYLE=\"color:#%2$s\">Ошибка при создании каталога для логов: %1$s\n</%3$s><BR>",
                        e.getMessage(),
                        TextFunc.ColorToRGBCode(Color.DARKRED),
                        "SPAN"));
            }
        
        messages.append(String.format("<%3$s STYLE=\"color:#%2$s\">Каталог для логов: %1$s\n</%3$s><BR>",
                                      LogDirectory,
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // определяем каталог для пресетов сообщений
        PresetsDirectory = String.format("%1$s%2$sPresets", MyWorkingDir, File.separator);
        File presetsDir = new File(PresetsDirectory);
        if (!presetsDir.exists() || !presetsDir.isDirectory())
            try
            {
                if (presetsDir.mkdir())
                    messages.append(String.format(
                            "<%3$s STYLE=\"color:#%2$s\">Создан каталог для шаблонов: %1$s\n</%3$s><BR>",
                            PresetsDirectory,
                            TextFunc.ColorToRGBCode(Color.DARKBLUE),
                            "SPAN"));
                else
                {
                    PresetsDirectory = MyWorkingDir;
                    messages.append(String.format(
                            "<%3$s STYLE=\"color:#%2$s\">Каталог для шаблонов создать не удалось, будет использоваться: %1$s\n</%3$s><BR>",
                            PresetsDirectory,
                            TextFunc.ColorToRGBCode(Color.DARKRED),
                            "SPAN"));
                }
            }
            catch (SecurityException e)
            {
                messages.append(String.format(
                        "<%3$s STYLE=\"color:#%2$s\">Ошибка при создании каталога для шаблонов: %1$s\n</%3$s><BR>",
                        e.getMessage(),
                        TextFunc.ColorToRGBCode(Color.DARKRED),
                        "SPAN"));
            }
        
        messages.append(String.format("<%3$s STYLE=\"color:#%2$s\">Каталог для шаблонов: %1$s\n</%3$s><BR>",
                                      PresetsDirectory,
                                      TextFunc.ColorToRGBCode(Color.DARKBLUE),
                                      "SPAN"));
        
        // загружаем профили Minecrafting
        PathMinecraftingProfiles = String.format("%1$s%2$sMinecraftingProfiles.json", MyWorkingDir, File.separator);
        LoadMinecraftingProfiles();
        
        // загружаем профили Mojang
        PathMojangProfiles = String.format("%1$s%2$sMojangProfiles.json", MyWorkingDir, File.separator);
        LoadMojangProfiles();
        
        // запуск всего приложения
        //launch(args);
        LauncherImpl.launchApplication(MyApplication.class, MyPreloader.class, args);
    }
    
    /**
     * Создаём логгер
     * @param fileLoggerON    вкл/выкл файловый вывод
     * @param consoleLoggerON вкл/выкл консольный вывод
     * @param encoding        кодировка
     */
    private static void CreateLogger(
            boolean fileLoggerON, boolean consoleLoggerON, boolean fileLoggerUnicName, String encoding, String sLevel)
    {
        System.out.println("Логгер файловый: " + String.valueOf(fileLoggerON));
        System.out.println("Логгер консольный: " + String.valueOf(consoleLoggerON));
        System.out.println("Глубина логирования: " + sLevel);
        
        Level level;
        switch (sLevel.toUpperCase())
        {
            case "OFF":
                level = Level.OFF;
                break;
            case "SEVERE":
                level = Level.SEVERE;
                break;
            case "WARNING":
                level = Level.WARNING;
                break;
            case "INFO":
                level = Level.INFO;
                break;
            case "CONFIG":
                level = Level.CONFIG;
                break;
            case "FINE":
                level = Level.FINE;
                break;
            case "FINER":
                level = Level.FINER;
                break;
            case "FINEST":
                level = Level.FINEST;
                break;
            case "All":
                level = Level.ALL;
                break;
            default:
                level = Level.ALL;
        }
        
        LOGGER = Logger.getLogger(MyApplication.class.getName());
        LOGGER.setUseParentHandlers(false); // отключаю дефолтные хэндлеры логера
        LOGGER.setLevel(level);
        
        if (fileLoggerON)
        {
            String logName = ApplicationControl.NAME.replace(" ", "");
            
            if (fileLoggerUnicName)
                logName = FileFunc.generateUnicName(logName + "_", "");
            
            LogApplicationFile = String.format("%1$s%2$s%3$s.log", MyWorkingDir, File.separator, logName);
            
            System.out.println("Файл лога приложения: " + LogApplicationFile);
            
            try
            {
                FileHandler fh = new FileHandler(LogApplicationFile);
                fh.setEncoding(encoding);
                fh.setFormatter(new MyLogFormatter());
                fh.setLevel(level);
                LOGGER.addHandler(fh);
                
            }
            catch (SecurityException e)
            {
                LOGGER.log(Level.SEVERE, "Не удалось создать файл лога из-за политики безопасности", e);
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.log(Level.SEVERE, String.format("Кодировка %1$s не поддерживается", encoding), e);
            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, "Не удалось создать файл лога из-за ошибки ввода-вывода", e);
            }
        }
        
        if (consoleLoggerON)
        {
            try
            {
                ConsoleHandler ch = new ConsoleHandler();
                ch.setEncoding(encoding);
                ch.setFormatter(new MyLogFormatter());
                ch.setLevel(level);
                LOGGER.addHandler(ch);
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.log(Level.SEVERE, String.format("Кодировка %1$s не поддерживается", encoding), e);
            }
        }
    }
    
    /**
     * загрузка настроек приложения
     */
    private static void LoadConfig(String configFile)
    {
        config = new Config();
        Object o = JsonFunc.loadObjectFormFile(configFile, config);
        
        if (o instanceof Config)
            config = (Config) o;
    }
    
    // загрузка профилей игроков Minecrafting
    private static void LoadMinecraftingProfiles()
    {
        Object o = JsonFunc.loadObjectFormFile(PathMinecraftingProfiles, minecraftingProfiles);
        
        if (o instanceof MinecraftingProfiles)
        {
            minecraftingProfiles = (MinecraftingProfiles) o;
            
            messages.append(String.format(
                    "<%3$s STYLE=\"color:#%2$s\">Профили игроков Minecrafting загружены успешно: %1$s\n</%3$s><BR>",
                    PathMinecraftingProfiles,
                    TextFunc.ColorToRGBCode(Color.DARKGREEN),
                    "SPAN"));
            messages.append(String.format(
                    "<%3$s STYLE=\"color:#%2$s\">Обнаружено профилей игроков Minecrafting: %1$d\n</%3$s><BR>",
                    minecraftingProfiles.size(),
                    TextFunc.ColorToRGBCode(Color.DARKBLUE),
                    "SPAN"));
        }
        else
        {
            messages.append(String.format(
                    "<%3$s STYLE=\"color:#%2$s\">Проблемы при загрузке профилей игроков Minecrafting: %1$s\n</%3$s><BR>",
                    PathMinecraftingProfiles,
                    TextFunc.ColorToRGBCode(Color.DARKRED),
                    "SPAN"));
            LOGGER.log(Level.SEVERE,
                       String.format("Проблемы при загрузке профилей игроков Minecrafting: %1$s",
                                     PathMinecraftingProfiles));
        }
    }
    
    /**
     * загрузка профилей игроков Mojang
     */
    private static void LoadMojangProfiles()
    {
        Object o = JsonFunc.loadObjectFormFile(PathMojangProfiles, mojangProfiles);
        
        if (o instanceof MojangProfiles)
        {
            mojangProfiles = (MojangProfiles) o;
            
            messages.append(String.format(
                    "<%3$s STYLE=\"color:#%2$s\">Профили игроков Mojang загружены успешно: %1$s\n</%3$s><BR>",
                    PathMojangProfiles,
                    TextFunc.ColorToRGBCode(Color.DARKGREEN),
                    "SPAN"));
            messages.append(String.format(
                    "<%3$s STYLE=\"color:#%2$s\">Обнаружено профилей игроков Mojang: %1$d\n</%3$s><BR>",
                    mojangProfiles.size(),
                    TextFunc.ColorToRGBCode(Color.DARKBLUE),
                    "SPAN"));
            
        }
        else
        {
            messages.append(String.format(
                    "<%3$s STYLE=\"color:#%2$s\">Проблемы при загрузке профилей игроков Mojang: %1$s\n</%3$s><BR>",
                    PathMojangProfiles,
                    TextFunc.ColorToRGBCode(Color.DARKRED),
                    "SPAN"));
            LOGGER.log(Level.SEVERE,
                       String.format("Проблемы при загрузке профилей игроков Mojang: %1$s", PathMojangProfiles));
        }
    }
    
    /**
     * завершение работы приложения
     */
    private void Exit(WindowEvent event)
    {
        if (!ShowConfirmationDialog("Подтвердите", null, "Закончить работу?", null))
            event.consume();
    }
    
    /**
     * действия в самом конце
     */
    @Override
    public void stop()
    {
        statusChecker.Stop();
        scene.DisconnectFromServer();
        
        config.MainWindowHeight = scene.getHeight();
        config.MainWindowWidth = scene.getWidth();
        config.MainSplitPaneLocation = scene.spHorMain.getDividerPositions();
        config.ContentSplitPaneLocation = scene.spVerMain.getDividerPositions();
        config.MessageSplitPaneLocation = scene.spMsgMain.getDividerPositions();
        
        if (!JsonFunc.saveObjectToFile(ConfigFilePath, config))
            LOGGER.log(Level.SEVERE,
                       String.format("Файл настроек приложения сохранить не удалось: %1$s", ConfigFilePath));
        
        // сохраняем БД Minecrafting
        minecraftingProfiles.SanctionsBuffer = null;
        minecraftingProfiles.IPBuffer = null;
        if (!JsonFunc.saveObjectToFile(PathMinecraftingProfiles, minecraftingProfiles))
            LOGGER.log(Level.SEVERE,
                       String.format("Файл профилей игроков Minecrafting записать не удалось: \"%1$s\"",
                                     PathMinecraftingProfiles));
        
        // сохраняем БД Mojang
        if (!JsonFunc.saveObjectToFile(PathMojangProfiles, mojangProfiles))
            LOGGER.log(Level.SEVERE,
                       String.format("Файл профилей игроков Mojang записать не удалось: \"%1$s\"", PathMojangProfiles));
        
        LOGGER.log(Level.FINE, "Пока!");
    }
    
    /**
     * старт основной сцены
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        scene = new MainScene(new BorderPane(), config.MainWindowWidth, config.MainWindowHeight);
        stage.setScene(scene);
        stage.setAlwaysOnTop(config.AlwaysOnTop);
        stage.getIcons().add(ResFunc.getImage("mainicon128"));
        stage.setTitle(NAME + " " + VERSION);
        checkAndLoadCSS();
        stage.setOnCloseRequest(this::Exit);
        stage.setOnShown(lambda -> {
            // судя по всему - проблема в Линкусе только
            scene.getWindow().setHeight(config.MainWindowHeight);
            scene.getWindow().setWidth(config.MainWindowWidth);
            
            // попытка решить проблему некорректного определения координат
            // окна при старте приложения (вывод основного меню)
            scene.getWindow().setX(scene.getWindow().getX());
            if (config.ShowAccessDialogAtStart)
            {
                Runnable SendDataToApplication = () -> scene.ConnectToServer();
                Platform.runLater(SendDataToApplication);
            }
        });
        
        stage.show();
    }
    
    /**
     * Работа с прелоадером
     */
    @Override
    public void init() throws Exception
    {
        int time = config.SplashScreenTime;
        for (int i = time; i > 0; i--)
        {
            LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification((double) i / time));
        }
    }
}