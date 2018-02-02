package mcru.MinecraftingTools;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mcru.MinecraftingTools.Dialogs.*;
import mcru.MinecraftingTools.Functions.DesktopFunc;
import mcru.MinecraftingTools.Functions.FileFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.*;
import mcru.MinecraftingTools.Interface.ContentPane;
import mcru.MinecraftingTools.Interface.HorizontalSpacer;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.Interface.MyTab;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.MinecraftingAPI.MC_Connection;
import mcru.MinecraftingTools.MinecraftingAPI.MC_Message;
import mcru.MinecraftingTools.MinecraftingAPI.MinecraftingDBListBuilder;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfile;
import mcru.MinecraftingTools.MinecraftingAPI.TokenFunc;
import mcru.MinecraftingTools.MojangAPI.MojangDBListBuilder;
import mcru.MinecraftingTools.MojangAPI.MojangProfileWebSearcher;
import mcru.MinecraftingTools.MojangAPI.MojangServerStatusChecker;
import mcru.MinecraftingTools.Sound.SoundPlayer;
import ontando.minecrafting.remote_access.env.DataValue;
import ontando.minecrafting.remote_access.network.packet.data.PerformActionPacket;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Dialogs.CommonDialogs.ShowConfirmationDialog;
import static mcru.MinecraftingTools.Functions.DesktopFunc.openWebResource;
import static mcru.MinecraftingTools.Functions.TextFunc.*;
import static mcru.MinecraftingTools.Helpers.StyleManager.getBaseStyle;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Основная сцена приложения Minecrafting Tools
 */
public class MainScene extends Scene
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    final private KeyCombination SHIFT_ENTER = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
    final private KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
    final private KeyCombination CTRL_P = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    public SplitPane spHorMain;
    public SplitPane spVerMain;
    public SplitPane spMsgMain;
    public ContentPane logContent;
    public TabPane tabpaneContent;
    public Label labelServerStatus;
    public Label labelPacketCount;
    public ProgressBar progressBar;
    public ListView <ChannelListElement> listChannels = new ListView <>();
    public ListView <PlayerListElement> listPlayers = new ListView <>();
    public Tooltip CTRL_Tooltip = new Tooltip();
    public Button buttonSendMessage;
    public FlowPane paneStatus;
    private Button buttonPlayerInfo;
    private Button buttonPlayerNewTab;
    private Button buttonPlayerMojRequest;
    private Button buttonPlayerMcruRequest;
    private Button buttonChannelInfo;
    private Button buttonChannelNewTab;
    private Button buttonChannelLeaveGroups;
    private Button buttonOptionsSendMessage;
    private BorderPane root;
    private TabPane tabpaneChannelsPlayers;
    private TextArea textareaSendMessage;
    private Label labelSendMessage;
    private Label labelServerName;
    private Label labelTextMessageStatus;
    private Label labelSelectedPlayer;
    private Label labelSelectedChannel;
    
    public MainScene(Parent root, double width, double height)
    {
        super(root, width, height);
        
        if (!(root instanceof BorderPane))
        {
            logger.log(Level.SEVERE, "Ошибка, которой никогда не должно быть");
            System.exit(0);
        }
        
        this.root = (BorderPane) root;
        this.root.setBorder(new Border(new MyBorder(6, 2)));
        MenuBar mainMenuBar = new MenuBar();
        Menu actionMenu = new Menu("_Действия");
        
        // подключиться к серверу
        MenuItem connectItem = new MenuItem("Подключиться к серверу", new ImageView(ResFunc.getImage("key24")));
        connectItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        connectItem.setOnAction(lambda -> ConnectToServer());
        
        // разорвать соединение
        MenuItem disconnectItem = new MenuItem("Разорвать соединение", new ImageView(ResFunc.getImage("cancel24")));
        disconnectItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        disconnectItem.setOnAction(lambda -> {
            if (connection == null || connection.client == null || !connection.client.isActive())
                return;
            
            if (ShowConfirmationDialog("Подтвердите", null, "Разорвать текущее соединение с сервером?", null))
                DisconnectFromServer();
        });
        
        // Сохранить всё
        MenuItem saveItem = new MenuItem("Сохранить текущий чат", new ImageView(ResFunc.getImage("save24")));
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveItem.setOnAction(lambda -> saveCurrentChatTab());
        
        // открыть лог текущего чата
        MenuItem log_chatItem = new MenuItem("Открыть лог чатa", new ImageView(ResFunc.getImage("text24")));
        log_chatItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        log_chatItem.setOnAction(lambda -> openLogChat());
        
        // поиск
        MenuItem findItem = new MenuItem("Искать в текущем чате", new ImageView(ResFunc.getImage("search24")));
        findItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        findItem.setOnAction(lambda -> searchOnCurrentChatTab());
        
        // Создать вкладку для веб-навигации
        MenuItem webItem = new MenuItem("Создать вкладку веб-навигации", new ImageView(ResFunc.getImage("www24")));
        webItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
        webItem.setOnAction(lambda -> addWebContentTab(""));
        
        // выход
        MenuItem exitItem = new MenuItem("Закончить работу", new ImageView(ResFunc.getImage("exit24")));
        exitItem.setOnAction(lambda -> Exit());
        
        // обновить
        MenuItem repaintItem = new MenuItem("Обновить текущий чат", new ImageView(ResFunc.getImage("update24")));
        repaintItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        repaintItem.setOnAction(lambda -> getCurrentChatContentPane().repaintAllContent());
        
        actionMenu.getItems().addAll(connectItem,
                                     disconnectItem,
                                     new SeparatorMenuItem(),
                                     webItem,
                                     new SeparatorMenuItem(),
                                     findItem,
                                     saveItem,
                                     log_chatItem,
                                     new SeparatorMenuItem(),
                                     repaintItem,
                                     new SeparatorMenuItem(),
                                     exitItem);
        
        //меню Управление
        Menu controlMenu = new Menu("_Управление");
        
        // Настройки
        MenuItem setupItem = new MenuItem("Настройки", new ImageView(ResFunc.getImage("setup24")));
        setupItem.setOnAction(lambda -> new ConfigDialog(getWindow()));
        
        //Подсветки
        MenuItem highlightItem = new MenuItem("Подсветки", new ImageView(ResFunc.getImage("highlight24")));
        highlightItem.setOnAction(lambda -> new ConfigHighlightDialog(getWindow()));
        
        // каталог с шаблонами
        MenuItem op_shablonItem =
                new MenuItem("Открыть каталог с шаблонами", new ImageView(ResFunc.getImage("open_catalog24")));
        op_shablonItem.setOnAction(lambda -> DesktopFunc.exploreFile(new File(PresetsDirectory)));
        
        // каталог с логами
        MenuItem op_logsItem =
                new MenuItem("Открыть каталог с логами", new ImageView(ResFunc.getImage("open_catalog24")));
        op_logsItem.setOnAction(lambda -> DesktopFunc.exploreFile(new File(LogDirectory)));
        
        // Управление аутентификационными данными
        MenuItem auth_setupItem =
                new MenuItem("Управление аутентификационными данными", new ImageView(ResFunc.getImage("lock24")));
        auth_setupItem.setOnAction(lambda -> AuthentificationSetup());
        
        controlMenu.getItems().addAll(setupItem,
                                      highlightItem,
                                      new SeparatorMenuItem(),
                                      op_shablonItem,
                                      op_logsItem,
                                      new SeparatorMenuItem(),
                                      auth_setupItem);
        
        //меню Инструменты
        Menu toolsMenu = new Menu("_Инструменты");
        
        // статусы серверов Можанг
        MenuItem serv_statItem =
                new MenuItem("Проверить статусы серверов Mojang", new ImageView(ResFunc.getImage("server_status24")));
        serv_statItem.setOnAction(lambda -> checkMojangServerStatus());
        
        // /Просмотр БД Можанг
        MenuItem dbMojangItem =
                new MenuItem("Просмотреть локальную БД Mojang", new ImageView(ResFunc.getImage("users24")));
        dbMojangItem.setOnAction(lambda -> showLocalDBMojang());
        
        // /Просмотр БД Майнкрафтинг
        MenuItem dbMCRUItem =
                new MenuItem("Просмотреть локальную БД Minecrafting", new ImageView(ResFunc.getImage("users24")));
        dbMCRUItem.setOnAction(lambda -> showLocalDBMinecrafting());
        
        toolsMenu.getItems().addAll(dbMojangItem, dbMCRUItem, new SeparatorMenuItem(), serv_statItem);
        
        //меню Информация
        Menu infoMenu = new Menu("_Информация");
        MenuItem info_MCRUItem = new MenuItem("Minecrafting.ru", new ImageView(ResFunc.getImage("url24")));
        info_MCRUItem.setOnAction(lambda -> DesktopFunc.openWebResource(MinecraftingURL, false));
        
        MenuItem info_HelpItem = new MenuItem("Справка", new ImageView(ResFunc.getImage("help24")));
        info_HelpItem.setOnAction(lambda -> DesktopFunc.exploreFile(new File(String.format("%1$s%2$sHelp%2$s%3$s",
                                                                                           MyWorkingDir,
                                                                                           File.separator,
                                                                                           config.HelpFileName))));
        
        MenuItem info_MTItem = new MenuItem(NAME, new ImageView(ResFunc.getImage("url24")));
        info_MTItem.setOnAction(lambda -> DesktopFunc.openWebResource(MinecraftingToolsURL, false));
        
        MenuItem info_ProtocolItem = new MenuItem("Протокол MCRU", new ImageView(ResFunc.getImage("info24")));
        info_ProtocolItem.setOnAction(lambda -> {
            if (!MainScene.this.isConnection(true))
                return;
            
            CommonDialogs.ShowLongMessage(Alert.AlertType.INFORMATION, "Протокол", null, connection.dumpProtocol());
        });
        
        MenuItem info_AboutItem = new MenuItem("Об этом", new ImageView(ResFunc.getImage("info24")));
        info_AboutItem.setOnAction(lambda -> new AboutDialog());
        
        infoMenu.getItems().addAll(info_AboutItem,
                                   new SeparatorMenuItem(),
                                   info_HelpItem,
                                   info_MCRUItem,
                                   info_MTItem,
                                   new SeparatorMenuItem(),
                                   info_ProtocolItem);
        
        // панель вкладок с содержанием
        tabpaneContent = new TabPane();
        tabpaneContent.setFocusTraversable(false);
        tabpaneContent.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            // меняю отображение на стандартное (снимаю подсветку)
            ((MyTab) newTab).applyStyle(newTab.isSelected());
            
            // выполняю скрипты
            ContentPane cp = (ContentPane) newTab.getContent();
            cp.repaintActiveContent();
        });
        addContentTab("Весь чат",
                      new ImageView(ResFunc.getImage("asterisk16")).getImage(),
                      false,
                      new ContentFilter(null, null));
        
        // метка отправки сообщений
        labelSendMessage = new Label(ApplicationControl.notFound);
        labelSendMessage.setScaleX(config.SendMessageLabelScale);
        labelSendMessage.setScaleY(config.SendMessageLabelScale);
        labelSendMessage.setBorder(new Border(new MyBorder(3)));
        labelSendMessage.getStyleClass().add("PANEL-SPECIAL");
        labelSendMessage.setAlignment(Pos.CENTER);
        
        // кнопка опций отправки
        buttonOptionsSendMessage = new Button("", new ImageView(ResFunc.getImage("down_list24")));
        buttonOptionsSendMessage.setTooltip(new Tooltip("Опции сообщения"));
        buttonOptionsSendMessage.setFocusTraversable(false);
        buttonOptionsSendMessage.setOnAction(lambda -> OptionsSendMessageClick());
        
        // текстовое поле для отправки сообщений
        textareaSendMessage = new TextArea();
        textareaSendMessage.setFocusTraversable(false);
        textareaSendMessage.setWrapText(true);
        textareaSendMessage.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        textareaSendMessage.setPrefColumnCount(1);
        textareaSendMessage.setBorder(new Border(new MyBorder(6, 2)));
        textareaSendMessage.textProperty()
                           .addListener((observable, oldValue, newValue) -> setTextMessageStatus(newValue));
        textareaSendMessage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (SHIFT_ENTER.match(event) && config.ShiftEnterAsNewLine)
            {
                textareaSendMessage.deleteText(textareaSendMessage.getSelection());
                textareaSendMessage.insertText(textareaSendMessage.getCaretPosition(), "\n");
                event.consume();
            }
            else if (SHIFT_ENTER.match(event) && !config.ShiftEnterAsNewLine)
            {
                buttonSendMessage.fire();
                event.consume();
            }
            else if (ENTER.match(event) && config.ShiftEnterAsNewLine)
            {
                buttonSendMessage.fire();
                event.consume();
            }
        });
        // кнопка отправки сообщений
        buttonSendMessage = new Button("", new ImageView(ResFunc.getImage("message24")));
        buttonSendMessage.setOnAction(lambda -> sendMessage());
        if (config.ShiftEnterAsNewLine)
            buttonSendMessage.setTooltip(new Tooltip("Отправить сообщение\n[Enter]"));
        else
            buttonSendMessage.setTooltip(new Tooltip("Отправить сообщение\n[Shift+Enter]"));
        
        //интерфейс отправки сообщений
        VBox vboxSendMessage = new VBox(1);
        vboxSendMessage.setAlignment(Pos.CENTER);
        
        HBox hboxSendMessage = new HBox(3);
        hboxSendMessage.setAlignment(Pos.CENTER);
        hboxSendMessage.setBorder(new Border(new MyBorder(3)));
        hboxSendMessage.getChildren().addAll(buttonOptionsSendMessage, textareaSendMessage, buttonSendMessage);
        
        vboxSendMessage.getChildren().addAll(labelSendMessage, hboxSendMessage);
        
        // панель с разделителем для сообщений
        spMsgMain = new SplitPane(tabpaneContent, vboxSendMessage);
        SplitPane.setResizableWithParent(vboxSendMessage, false);
        spMsgMain.setOrientation(Orientation.VERTICAL);
        spMsgMain.setDividerPositions(config.MessageSplitPaneLocation);
        
        ////// панель вкладок с каналами и игроками
        //// каналы
        // кнопки
        // информация
        buttonChannelInfo = new Button("", new ImageView(ResFunc.getImage("info24")));
        buttonChannelInfo.setDisable(true);
        buttonChannelInfo.setFocusTraversable(false);
        buttonChannelInfo.setOnAction(lambda -> showInfoChannel());
        buttonChannelInfo.setTooltip(new Tooltip("Информация о канале\n[Ctrl+Q]"));
        
        //создать закладку
        buttonChannelNewTab = new Button("", new ImageView(ResFunc.getImage("yellow_pin24")));
        buttonChannelNewTab.setFocusTraversable(false);
        buttonChannelNewTab.setDisable(true);
        buttonChannelNewTab.setTooltip(new Tooltip("Создать отдельную вкладку\nдля этого канала\n[Ctrl+Z]"));
        buttonChannelNewTab.setOnAction(lambda -> ChannelNewTabClick());
        
        //создать покинуть все группы
        buttonChannelLeaveGroups = new Button("", new ImageView(ResFunc.getImage("leave24")));
        buttonChannelLeaveGroups.setFocusTraversable(false);
        buttonChannelLeaveGroups.setDisable(true);
        buttonChannelLeaveGroups.setTooltip(new Tooltip("Покинуть все группы"));
        buttonChannelLeaveGroups.setOnAction(lambda -> leaveAllParties(true));
        
        // кнопка сортировки каналов
        Button buttonSortChannelList = new Button("", new ImageView(ResFunc.getImage("sort24")));
        buttonSortChannelList.setTooltip(new Tooltip("Сортировать каналы"));
        buttonSortChannelList.setFocusTraversable(false);
        buttonSortChannelList.setOnAction(lambda -> ChannelsButtonSortClick(buttonSortChannelList));
        
        // панель кнопок
        ToolBar tbChannels = new ToolBar();
        tbChannels.getItems().addAll(buttonChannelInfo,
                                     buttonChannelLeaveGroups,
                                     new HorizontalSpacer(),
                                     buttonChannelNewTab,
                                     buttonSortChannelList);
        
        // список
        listChannels.setBorder(new Border(new MyBorder(2)));
        listChannels.setOrientation(Orientation.VERTICAL);
        listChannels.setEditable(false);
        listChannels.setCellFactory(param -> new ChannelListCell());
        listChannels.getSelectionModel().
                selectedItemProperty().
                            addListener((ObservableValue <? extends ChannelListElement> ov, ChannelListElement old_val, ChannelListElement new_val) -> selectChannelListElement(
                                    new_val));
        
        // панель с кнопками и списком
        BorderPane paneChannels = new BorderPane();
        paneChannels.setBorder(new Border(new MyBorder(5, 1)));
        paneChannels.setTop(tbChannels);
        paneChannels.setCenter(listChannels);
        
        // вкладка
        Tab tabChannels = new Tab("Каналы");
        tabChannels.setGraphic(new ImageView(ResFunc.getImage("channel20")));
        tabChannels.setContent(paneChannels);
        tabChannels.setClosable(false);
        
        //// игроки
        // кнопки
        // информация
        buttonPlayerInfo = new Button("", new ImageView(ResFunc.getImage("info24")));
        buttonPlayerInfo.setDisable(true);
        buttonPlayerInfo.setFocusTraversable(false);
        buttonPlayerInfo.setOnAction(lambda -> showInfoPlayer());
        buttonPlayerInfo.setTooltip(new Tooltip("Информация об игроке\n[Ctrl+Q]"));
        
        //создать закладку
        buttonPlayerNewTab = new Button("", new ImageView(ResFunc.getImage("yellow_pin24")));
        buttonPlayerNewTab.setFocusTraversable(false);
        buttonPlayerNewTab.setDisable(true);
        buttonPlayerNewTab.setOnAction(lambda -> PlayerNewTabClick());
        buttonPlayerNewTab.setTooltip(new Tooltip("Создать отдельную вкладку\nдля этого игрока\n[Ctrl+Z]"));
        
        //отправить запрос в Mojang
        buttonPlayerMojRequest = new Button("", new ImageView(ResFunc.getImage("mojang24")));
        buttonPlayerMojRequest.setFocusTraversable(false);
        buttonPlayerMojRequest.setDisable(true);
        buttonPlayerMojRequest.setOnAction(lambda -> Mojang_Request());
        buttonPlayerMojRequest.setTooltip(new Tooltip("Отправить запрос об игроке\nв MojangAPI"));
        
        //отправить запрос по игроку в minecrafting.ru
        buttonPlayerMcruRequest = new Button("", new ImageView(ResFunc.getImage("mcru24")));
        buttonPlayerMcruRequest.setFocusTraversable(false);
        buttonPlayerMcruRequest.setDisable(true);
        buttonPlayerMcruRequest.setOnAction(lambda -> MCRU_Request());
        buttonPlayerMcruRequest.setTooltip(new Tooltip("Отправить запрос об игроке\nв Minecrafting.ru"));
        
        //искать игрока в БД minecrafting.ru
        Button buttonPlayerMcruSearch = new Button("", new ImageView(ResFunc.getImage("finddata24")));
        buttonPlayerMcruSearch.setFocusTraversable(false);
        buttonPlayerMcruSearch.setOnAction(lambda -> MCRU_Search());
        buttonPlayerMcruSearch.setTooltip(new Tooltip("Искать игрока\nв Minecrafting.ru"));
        
        // кнопка сортировки игроков
        Button buttonSortPlayerList = new Button("", new ImageView(ResFunc.getImage("sort24")));
        buttonSortPlayerList.setTooltip(new Tooltip("Сортировать игроков"));
        buttonSortPlayerList.setFocusTraversable(false);
        buttonSortPlayerList.setOnAction(lambda -> PlayersButtonSortClick(buttonSortPlayerList));
        
        // панель кнопок
        ToolBar tbPlayers = new ToolBar();
        tbPlayers.getItems().addAll(buttonPlayerInfo,
                                    buttonPlayerMojRequest,
                                    buttonPlayerMcruRequest,
                                    buttonPlayerMcruSearch,
                                    new HorizontalSpacer(),
                                    buttonPlayerNewTab,
                                    buttonSortPlayerList);
        
        // список
        listPlayers.setBorder(new Border(new MyBorder(2)));
        listPlayers.setOrientation(Orientation.VERTICAL);
        listPlayers.setEditable(false);
        listPlayers.setCellFactory(param -> new PlayerListCell());
        listPlayers.getSelectionModel().selectedItemProperty()
                   .addListener((ObservableValue <? extends PlayerListElement> ov, PlayerListElement old_ple, PlayerListElement new_ple) -> selectPlayerListElement(
                           new_ple));
        
        // панель с кнопками и списком
        BorderPane panePlayers = new BorderPane();
        panePlayers.setBorder(new Border(new MyBorder(5, 1)));
        panePlayers.setTop(tbPlayers);
        panePlayers.setCenter(listPlayers);
        
        // вкладка
        Tab tabPlayers = new Tab("Игроки");
        tabPlayers.setGraphic(new ImageView(ResFunc.getImage("black_user16")));
        tabPlayers.setContent(panePlayers);
        tabPlayers.setClosable(false);
        
        // панель вкладок
        tabpaneChannelsPlayers = new TabPane();
        tabpaneChannelsPlayers.setFocusTraversable(false);
        tabpaneChannelsPlayers.getTabs().addAll(tabChannels, tabPlayers);
        tabpaneChannelsPlayers.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if (tabpaneChannelsPlayers.getSelectionModel().isSelected(1))
                listPlayers.getSelectionModel().clearSelection();
            
            if (tabpaneChannelsPlayers.getSelectionModel().isSelected(0))
            {
                ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
                if (cle == null)
                    return;
                
                labelSendMessage.setText(cle.name);
            }
        });
        
        // панель с разделителем основная Вертекальная
        spVerMain = new SplitPane(tabpaneChannelsPlayers, spMsgMain);
        SplitPane.setResizableWithParent(tabpaneChannelsPlayers, false);
        spVerMain.setOrientation(Orientation.HORIZONTAL);
        spVerMain.setDividerPositions(config.ContentSplitPaneLocation);
        
        // панель с разделителем основная горизонтальная
        logContent = new ContentPane("Системные события",
                                     ApplicationControl.config.Encoding,
                                     null,
                                     false,
                                     MyApplication.messages.append("<HR><BR>").toString());
        spHorMain = new SplitPane(spVerMain, logContent);
        SplitPane.setResizableWithParent(logContent, false);
        spHorMain.setOrientation(Orientation.VERTICAL);
        spHorMain.setDividerPositions(config.MainSplitPaneLocation);
        
        // статус сервера
        labelServerStatus = new Label();
        labelServerStatus.setText(" ");
        labelServerStatus.setContentDisplay(ContentDisplay.RIGHT);
        labelServerStatus.setGraphic(new ImageView(ResFunc.getImage("led_grey16")));
        
        // имя сервера
        labelServerName = new Label();
        labelServerName.setText("Сервер не указан");
        labelServerName.setTooltip(new Tooltip("Статус связи с сервером"));
        
        // статус соединения
        labelPacketCount = new Label();
        labelPacketCount.setText("0");
        labelPacketCount.setGraphic(new ImageView(ResFunc.getImage("update16")));
        labelPacketCount.setTooltip(new Tooltip("Статус обмена с сервером"));
        
        // статус поля редактирования
        labelTextMessageStatus = new Label();
        labelTextMessageStatus.setText("0 знаков");
        labelTextMessageStatus.setTooltip(new Tooltip("Статус поля редактора"));
        labelTextMessageStatus.setGraphic(new ImageView(ResFunc.getImage("edit16")));
        
        // статус выбранный игрок
        labelSelectedPlayer = new Label();
        labelSelectedPlayer.setText(ApplicationControl.notFound);
        labelSelectedPlayer.setTooltip(new Tooltip("Выбранный игрок"));
        labelSelectedPlayer.setGraphic(new ImageView(ResFunc.getImage("black_user16")));
        
        // статус выбранный канал
        labelSelectedChannel = new Label();
        labelSelectedChannel.setText(ApplicationControl.notFound);
        labelSelectedChannel.setTooltip(new Tooltip("Выбранный канал"));
        labelSelectedChannel.setGraphic(new ImageView(ResFunc.getImage("channel20")));
        
        // прогрессбар процессов
        progressBar = new ProgressBar();
        progressBar.setCenterShape(true);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(100);
        
        paneStatus = new FlowPane(labelServerStatus,
                                  new Separator(Orientation.VERTICAL),
                                  labelServerName,
                                  new Separator(Orientation.VERTICAL),
                                  labelPacketCount,
                                  new Separator(Orientation.VERTICAL),
                                  labelTextMessageStatus,
                                  new Separator(Orientation.VERTICAL),
                                  labelSelectedPlayer,
                                  new Separator(Orientation.VERTICAL),
                                  labelSelectedChannel,
                                  new Separator(Orientation.VERTICAL),
                                  progressBar);
        paneStatus.setAlignment(Pos.CENTER_LEFT);
        paneStatus.setPrefWrapLength(Integer.MAX_VALUE);
        
        // обработка нажатия CTRL
        setOnKeyPressed(event -> {
            CTRLisPressed = event.isControlDown();
            CTRL_Key_Release(event.getCode());
        });
        setOnKeyReleased(ke -> CTRLisPressed = false);
        
        // перехват прокрутки колеса мыши
        addEventFilter(ScrollEvent.SCROLL, event -> {
            // масштабирование шрифта
            if (config.MouseWealZooming && event.isControlDown())
            {
                setFontScale(event.getDeltaY());
                event.consume();
            }
        });
        
        // перехват нажатия клавиш
        addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            // масштабирование шрифта
            if (new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN).match(event) ||
                new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN).match(event))
            {
                if (ApplicationControl.config.CommonFontSize >= 100)
                    return;
                setFontScale(100);
                event.consume();
            }
            // масштабирование шрифта
            else if (new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN).match(event) ||
                     new KeyCodeCombination(KeyCode.UNDERSCORE, KeyCombination.CONTROL_DOWN).match(event))
            {
                if (ApplicationControl.config.CommonFontSize <= 10)
                    return;
                setFontScale(-100);
                event.consume();
            }
            
            // поиск игрока в текущем чате
            if (CTRL_P.match(event))
            {
                PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
                if (ple != null)
                    getCurrentChatContentPane().searchText(ple.nick);
            }
        });
        
        mainMenuBar.getMenus().addAll(actionMenu, controlMenu, toolsMenu, infoMenu);
        this.root.setTop(mainMenuBar);
        this.root.setCenter(spHorMain);
        this.root.setBottom(paneStatus);
        
        // запускаю проверку статуса соединения
        statusChecker = new StatusChecker(config.ServerAccessCheckInterval);
        statusChecker.start();
    }
    
    /**
     * обработка CTRL+Key
     */
    private void CTRL_Key_Release(KeyCode keyCode)
    {
        if (keyCode == KeyCode.Q)
        {
            CTRLisPressed = false;
            if (tabpaneChannelsPlayers.getSelectionModel().isSelected(0))
                buttonChannelInfo.fire();
            else if (tabpaneChannelsPlayers.getSelectionModel().isSelected(1))
                buttonPlayerInfo.fire();
        }
        else if (keyCode == KeyCode.Z)
        {
            CTRLisPressed = false;
            if (tabpaneChannelsPlayers.getSelectionModel().isSelected(0))
                buttonChannelNewTab.fire();
            else if (tabpaneChannelsPlayers.getSelectionModel().isSelected(1))
                buttonPlayerNewTab.fire();
        }
    }
    
    /**
     * Масштабировать интерфейс
     */
    private void setFontScale(double zoom)
    {
        double fsize = config.CommonFontSize;
        fsize += zoom / 100;
        
        if (fsize < 10 || fsize > 100)
            return;
        
        root.setStyle(getBaseStyle());
        
        double cp_scale = fsize / config.CommonFontSize;
        logContent.setFontScale(cp_scale);
        for (Tab tab : scene.tabpaneContent.getTabs())
        {
            ContentPane cp = (ContentPane) tab.getContent();
            
            if (cp.modeWebNavigation)
                continue;
            
            cp.setFontScale(cp_scale);
        }
        
        config.CommonFontSize = fsize;
    }
    
    /**
     * завершение работы приложения
     */
    private void Exit()
    {
        if (ShowConfirmationDialog("Подтвердите", null, "Закончить работу?", null))
            Platform.exit();
    }
    
    /**
     * клик по кнопке сортировки каналов
     */
    private void ChannelsButtonSortClick(Button button)
    {
        ContextMenu menu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("По названию");
        menuItem1.setOnAction(lambda -> sortChannels(1));
        
        MenuItem menuItem2 = new MenuItem("По ID");
        menuItem2.setOnAction(lambda -> sortChannels(0));
        
        MenuItem menuItem3 = new MenuItem("По типу");
        menuItem3.setOnAction(lambda -> sortChannels(2));
        
        MenuItem menuItem4 = new MenuItem("По количеству игроков");
        menuItem4.setOnAction(lambda -> sortChannels(3));
        
        menu.getItems().addAll(menuItem2, menuItem1, menuItem3, menuItem4);
        
        menu.show(button, Side.BOTTOM, 0, 0);
    }
    
    /**
     * клик по кнопке сортировки игроков
     */
    private void PlayersButtonSortClick(Button button)
    {
        ContextMenu menu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("По нику");
        menuItem1.setOnAction(lambda -> sortPlayers(1));
        
        MenuItem menuItem2 = new MenuItem("По ID");
        menuItem2.setOnAction(lambda -> sortPlayers(0));
        
        MenuItem menuItem3 = new MenuItem("По тревоге");
        menuItem3.setOnAction(lambda -> sortPlayers(2));
        
        MenuItem menuItem4 = new MenuItem("По статусу");
        menuItem4.setOnAction(lambda -> sortPlayers(3));
        
        menu.getItems().addAll(menuItem1, menuItem2, menuItem3, menuItem4);
        
        menu.show(button, Side.BOTTOM, 0, 0);
    }
    
    /**
     * Сортировка каналов
     */
    private void sortChannels(int index)
    {
        if (index < 0)
            return;
        
        switch (index)
        {
            case 0:
                listChannels.getItems().sort(Comparator.comparing(ChannelListElement::getId));
                break;
            case 1:
                listChannels.getItems().sort(Comparator.comparing(ChannelListElement::getName));
                break;
            case 2:
                listChannels.getItems().sort(Comparator.comparing(ChannelListElement::getType));
                break;
            case 3:
                listChannels.getItems().sort(Comparator.comparing(ChannelListElement::getPlayersCount).reversed());
                break;
            default:
                listChannels.getItems().sort(Comparator.comparing(ChannelListElement::getId));
        }
        channelsListSorting = index;
        
        int selectedIndex = listChannels.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1)
            listChannels.scrollTo(selectedIndex);
        else
            listChannels.scrollTo(0);
    }
    
    /**
     * Сортировка игроков
     */
    private void sortPlayers(int index)
    {
        if (index < 0)
            return;
        
        switch (index)
        {
            case 0:
                listPlayers.getItems().sort(Comparator.comparing(PlayerListElement::getId));
                break;
            case 1:
                listPlayers.getItems().sort(Comparator.comparing(PlayerListElement::getNick));
                break;
            case 2:
                listPlayers.getItems().sort(Comparator.comparing(PlayerListElement::isAlert).reversed());
                break;
            case 3:
                listPlayers.getItems().sort(Comparator.comparing(PlayerListElement::isOnline));
                break;
            default:
                listPlayers.getItems().sort(Comparator.comparing(PlayerListElement::getId));
        }
        playersListSorting = index;
        
        int selectedIndex = listPlayers.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1)
            listPlayers.scrollTo(selectedIndex);
        else
            listPlayers.scrollTo(0);
    }
    
    /**
     * добавить вкладку на панель вкладок основного содержания
     */
    private void addContentTab(String caption, Image icon, boolean closable, ContentFilter filter)
    {
        MyTab tab = new MyTab(caption);
        ContentPane contentPane = new ContentPane(caption, config.Encoding, tab, false, "");
        contentPane.filter = filter;
        tab.setContent(contentPane);
        tab.setClosable(closable);
        tab.setGraphic(new ImageView(icon));
        tabpaneContent.getTabs().add(tab);
    }
    
    /**
     * добавить вкладку на панель вкладок основного содержания для веб-навигации
     */
    private void addWebContentTab(String url)
    {
        MyTab tab = new MyTab("Новый");
        ContentPane contentPane = new ContentPane("", config.Encoding, tab, true, url);
        contentPane.filter = null;
        tab.setContent(contentPane);
        tab.setClosable(true);
        tab.setGraphic(new ImageView(ResFunc.getImage("www16")));
        tabpaneContent.getTabs().add(tab);
        tabpaneContent.getSelectionModel().select(tab);
    }
    
    /**
     * открыть веб-ссылку
     */
    public void openWebLink(String url)
    {
        if (url == null || url.isEmpty())
            return;
        
        if (CTRLisPressed)
        {
            CTRLisPressed = false;
            addWebContentTab(url);
        }
        else
            openWebResource(url, config.ToAddDefaultUrlProtocol);
    }
    
    /**
     * Открыть ссылку на канал
     */
    public void openChannelLink(long id)
    {
        ChannelListElement cle = getChannelListElementByID(id);
        if (cle != null)
        {
            if (!CTRLisPressed)
            {
                tabpaneChannelsPlayers.getSelectionModel().select(0);
                listChannels.getSelectionModel().select(cle);
                listChannels.requestFocus();
                listChannels.scrollTo(cle);
                selectChannelListElement(cle);
            }
            else
            {
                CTRLisPressed = false;
                new InfoChannelDialog(cle);
            }
        }
    }
    
    /**
     * Открыть ссылку на игрока по ID
     */
    public void openPlayerLinkByID(long id)
    {
        PlayerListElement ple = getPlayerListElementByID(id);
        if (ple != null)
        {
            if (!CTRLisPressed)
            {
                tabpaneChannelsPlayers.getSelectionModel().select(1);
                listPlayers.getSelectionModel().select(ple);
                listPlayers.requestFocus();
                listPlayers.scrollTo(ple);
                selectPlayerListElement(ple);
            }
            else
            {
                CTRLisPressed = false;
                new InfoPlayerDialog(ple);
            }
        }
    }
    
    /**
     * Открыть ссылку на игрока по UUID
     */
    public void openPlayerLinkByUUID(String uuid)
    {
        PlayerListElement ple = getPlayerListElementByUUID(uuid);
        if (ple != null)
        {
            if (!CTRLisPressed)
            {
                tabpaneChannelsPlayers.getSelectionModel().select(1);
                listPlayers.getSelectionModel().select(ple);
                listPlayers.requestFocus();
                listPlayers.scrollTo(ple);
                selectPlayerListElement(ple);
            }
            else
            {
                CTRLisPressed = false;
                new InfoPlayerDialog(ple);
            }
        }
    }
    
    /**
     * выбор элемента списка каналов
     */
    private void selectChannelListElement(ChannelListElement item)
    {
        if (item == null)
        {
            labelSendMessage.setText(ApplicationControl.notFound);
            buttonChannelNewTab.setDisable(true);
            buttonChannelInfo.setDisable(true);
            buttonChannelLeaveGroups.setDisable(true);
            listChannels.setContextMenu(null);
            Platform.runLater(() -> labelSelectedChannel.setText(ApplicationControl.notFound));
            return;
        }
        
        labelSendMessage.setText(item.name);
        buttonChannelNewTab.setDisable(false);
        buttonChannelInfo.setDisable(false);
        buttonChannelLeaveGroups.setDisable(false);
        listChannels.setContextMenu(createListChannelsContextMenu());
        Platform.runLater(() -> labelSelectedChannel.setText(item.name));
    }
    
    /**
     * получить {@link ChannelListElement} по ID
     */
    public ChannelListElement getChannelListElementByID(long id)
    {
        for (ChannelListElement cle : listChannels.getItems())
        {
            if (cle.id == id)
                return cle;
        }
        
        if (id > -1)
            logger.log(Level.SEVERE, String.format("Элемент списка каналов не найден: %1$d", id));
        return null;
    }
    
    /**
     * Добавить элемент в список каналов
     */
    public void addChannelToList(ChannelListElement cle)
    {
        listChannels.getItems().add(cle);
    }
    
    /**
     * Удалить элемент из списка каналов
     */
    public void removeChannelFromList(long id)
    {
        ChannelListElement cle = getChannelListElementByID(id);
        if (cle != null)
            listChannels.getItems().remove(cle);
    }
    
    /**
     * обновить информацию на вкладке с каналами
     */
    private void updateChannelsTab()
    {
        tabpaneChannelsPlayers.getTabs().get(0).setText(String.format("Каналы [%1$d]", listChannels.getItems().size()));
    }
    
    /**
     * получить {@link PlayerListElement} по ID
     */
    public PlayerListElement getPlayerListElementByID(long id)
    {
        for (PlayerListElement ple : listPlayers.getItems())
        {
            if (ple.id == id)
                return ple;
        }
        
        if (id > -1)
            logger.log(Level.SEVERE, String.format("Элемент списка игроков не найден: %1$d", id));
        return null;
    }
    
    /**
     * получить {@link PlayerListElement} по UUID
     */
    public PlayerListElement getPlayerListElementByUUID(String uuid)
    {
        for (PlayerListElement ple : listPlayers.getItems())
        {
            if (ple.uuid.equals(uuid))
                return ple;
        }
        
        logger.log(Level.SEVERE, String.format("Игрок с UUID = %1$s не найден", uuid));
        return null;
    }
    
    /**
     * Добавить элемент в список игроков
     */
    public void addPlayerToList(PlayerListElement ple)
    {
        listPlayers.getItems().add(ple);
    }
    
    /**
     * Удалить элемент из списка игроков
     */
    public void removePlayerFromList(long id)
    {
        PlayerListElement ple = getPlayerListElementByID(id);
        if (ple != null)
            listPlayers.getItems().remove(ple);
    }
    
    /**
     * обновить информацию на вкладке с игроками
     */
    private void updatePlayersTab()
    {
        int playersOnline = 0;
        for (PlayerListElement ple : listPlayers.getItems())
        {
            if ((ple.online_flags & 0x01) != 0)
                playersOnline++;
        }
        
        tabpaneChannelsPlayers.getTabs().get(1).setText(String.format("Игроки [%1$d/%2$d]",
                                                                      listPlayers.getItems().size(),
                                                                      playersOnline));
    }
    
    /**
     * выбор элемента списка игроков
     */
    private void selectPlayerListElement(PlayerListElement item)
    {
        if (item == null)
        {
            ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
            if (cle != null)
                labelSendMessage.setText(cle.name);
            else
                labelSendMessage.setText(ApplicationControl.notFound);
            
            buttonPlayerMcruRequest.setDisable(true);
            buttonPlayerMojRequest.setDisable(true);
            buttonPlayerInfo.setDisable(true);
            buttonPlayerNewTab.setDisable(true);
            listPlayers.setContextMenu(null);
            Platform.runLater(() -> labelSelectedPlayer.setText(ApplicationControl.notFound));
            return;
        }
        
        labelSendMessage.setText(item.nick);
        buttonPlayerMcruRequest.setDisable(false);
        buttonPlayerMojRequest.setDisable(false);
        buttonPlayerInfo.setDisable(false);
        buttonPlayerNewTab.setDisable(false);
        listPlayers.setContextMenu(createListPlayersContextMenu());
        Platform.runLater(() -> labelSelectedPlayer
                .setText(TextFunc.SetFixedSize(item.nick, config.ChannelPlayerListLength)));
    }
    
    /**
     * нажатие кнопки отправки сообщения
     */
    private void sendMessage()
    {
        if (textareaSendMessage.getText().isEmpty())
            return;
        
        // опции сообщения
        Map <String, Integer> messageOptions = new HashMap <>();
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
        
        // писать игроку
        if (tabpaneChannelsPlayers.getSelectionModel().getSelectedIndex() == 1 && ple != null)
        {
            messageOptions.put("user", (int) ple.id);
            logger.log(Level.INFO,
                       String.format("Отправка сообщения игроку %1$s, id = %2$d, индекс в списке = %3$d",
                                     ple.nick,
                                     ple.id,
                                     listPlayers.getSelectionModel().getSelectedIndex()));
        }
        // писать в канал
        else if (cle != null)
        {
            messageOptions.put("channel", (int) cle.id);
            logger.log(Level.INFO,
                       String.format("Отправка сообщения в канал %1$s, id = %2$d, индекс в списке = %3$d",
                                     cle.name,
                                     cle.id,
                                     listChannels.getSelectionModel().getSelectedIndex()));
        }
        
        // не определили кому
        if (messageOptions.isEmpty())
        {
            CommonDialogs.ShowMessage(Alert.AlertType.WARNING, "Ошибка", null, "Необходимо выбрать адресата!", null);
            return;
        }
        
        // создаём сообщение
        SendMessageToChat(messageOptions, textareaSendMessage.getText());
        textareaSendMessage.setText("");
    }
    
    /**
     * отправка сообщения в чат
     */
    private void SendMessageToChat(Map map, String message)
    {
        if (!isConnection(true))
            return;
        
        if (connection.chatMessageID < 0)
        {
            logger.log(Level.SEVERE, String.format("Ошибка протокола, chatMessageID = %1$d", connection.chatMessageID));
            return;
        }
        
        if (map == null || message == null)
        {
            logger.log(Level.SEVERE, "Переданы некорректные параметры");
            return;
        }
        
        try
        {
            String[] list = message.replace("\r", "").split("\n");
            
            for (String s : list)
            {
                if (!s.isEmpty())
                {
                    PerformActionPacket packet =
                            connection.clientEnvironmentManager.packAction(connection.chatMessageID, map, s);
                    connection.client.sendPacket(packet);
                }
            }
            
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка при отправке сообщения", e);
        }
    }
    
    /**
     * Соедениться с сервером: отобразить диалог и получить данные от пользователя
     */
    public void ConnectToServer()
    {
        // обнуляю счётчик попыток подключения всегда при ручном подключении
        countTryConnections = 0;
        
        if (isConnection(false))
        {
            if (ShowConfirmationDialog("Подтвердите", null, "Разорвать соединение с сервером?", null))
                DisconnectFromServer();
            else
                return;
        }
        
        AuthentificationData dataFromDialog = new AccessServerDialog(scene.getWindow()).show();
        if (dataFromDialog == null)
            return;
        
        if (dataFromDialog.getLocalToken().isEmpty())
        {
            dataFromDialog.setLocalToken(TokenFunc.generateTokenByUUID(config.ServerTokenLength));
            logger.log(Level.INFO, String.format("Присвоен локальный токен: %1$s", dataFromDialog.getLocalToken()));
        }
        else
            logger.log(Level.INFO, String.format("Обнаружен локальный токен: %1$s", dataFromDialog.getLocalToken()));
        
        currentAuthData = dataFromDialog;
        startAuthentification();
    }
    
    /**
     * Начать процедуру подключения к серверу
     */
    public void startAuthentification()
    {
        // начинаем процедуру подключения
        if (getStatusConnectToServer())
        {
            logContent.addMessage(new LogMessage(String.format("%1$s сейчас занят, попробуйте позже", NAME),
                                                 LogMessage.MESSAGE_ERROR));
            return;
        }
        setStatusConnectToServer(true);
        clearConnection();
        
        // текст в статусе и заголовке
        Platform.runLater(() -> labelServerName.setText(currentAuthData.getServerName()));
        ((Stage) getWindow()).setTitle(String.format("%1$s %2$s", NAME, VERSION));
        
        int port = Integer.parseInt(currentAuthData.getServerName()
                                                   .substring(currentAuthData.getServerName().lastIndexOf(":") + 1,
                                                              currentAuthData.getServerName().length()));
        String address = currentAuthData.getServerName().substring(0, currentAuthData.getServerName().lastIndexOf(":"));
        
        connection = new MC_Connection(address,
                                       port,
                                       currentAuthData.getUserName(),
                                       currentAuthData.getPassword(),
                                       currentAuthData.getServerAuthInformation(),
                                       currentAuthData.getLocalToken());
        
        logContent.addMessage(new LogMessage(String.format("Пытаемся подключиться к серверу \"%1$s\" ...",
                                                           currentAuthData.getServerName()), LogMessage.MESSAGE_INFO));
        try
        {
            int startResult = connection.start();
            if (startResult > -1)
            {
                if (startResult == 0)
                    logContent.addMessage(new LogMessage(String.format(
                            "Подключились к серверу успешно, аутентификация была пройдена ранее. Ваш токен [%1$s]",
                            currentAuthData.getLocalToken()), LogMessage.MESSAGE_SUCCESS));
                
                else if (startResult == 1)
                {
                    logContent.addMessage(new LogMessage(String.format(
                            "Подключились к серверу успешно, требуется аутентификация. Ваш токен [%1$s]",
                            currentAuthData.getLocalToken()), LogMessage.MESSAGE_SUCCESS));
                    
                    CommonDialogs.ShowMessage(Alert.AlertType.INFORMATION,
                                              "Требуется аутентификация",
                                              null,
                                              String.format("Необходимо пройти аутентификацию на сервере:\n%1$s\n" +
                                                            "Ваш токен [%2$s]",
                                                            currentAuthData.getServerName(),
                                                            currentAuthData.getLocalToken()),
                                              null);
                }
                
                LogChatFile = FileFunc.generateUnicName(LogDirectory + File.separator, ".txt");
                
                ArrayList <ContentElement> message = new ArrayList <>();
                message.add(new ContentElement("Логи чата будут писаться в файл "));
                message.add(new ContentElement(LogChatFile,
                                               new MyLink("file", TextFunc.SetPathToHTML(LogChatFile)).get())
                                    .addElementClass("info-message"));
                logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_INFO));
            }
            else
                logContent.addMessage(new LogMessage(String.format(
                        "Подключиться к серверу \"%1$s\" не удалось, отказ сервера или нет связи",
                        currentAuthData.getServerName()), LogMessage.MESSAGE_ERROR));
        }
        catch (URISyntaxException | InterruptedException e)
        {
            logContent.addMessage(new LogMessage(String.format("Ошибка подключения к серверу \"%1$s\"", e.getMessage()),
                                                 LogMessage.MESSAGE_ERROR));
        }
        
        setStatusConnectToServer(false);
        // текст в заголовке
        ((Stage) getWindow()).setTitle(String.format("%1$s %2$s >> %3$s >> %4$s",
                                                     NAME,
                                                     VERSION,
                                                     currentAuthData.getServerName(),
                                                     currentAuthData.getUserName()));
    }
    
    /**
     * разорвать связь с сервером
     */
    public void DisconnectFromServer()
    {
        // счётчик попыток подключения деактивирую
        countTryConnections = -1;
        
        if (!isConnection(false))
            return;
        
        if (config.LeaveAllPartiesAtDisconnect)
            leaveAllParties(false);
        
        connection.Disconnect();
    }
    
    /**
     * Действия при разрыве связи
     */
    public void clearConnection()
    {
        // чищу списки игроков и каналов
        listPlayers.getItems().clear();
        listChannels.getItems().clear();
        
        // закрываю все вкладки кроме основной и веб
        ArrayList <Tab> tabs = new ArrayList <>();
        for (Tab tab : tabpaneContent.getTabs())
        {
            ContentPane cp = (ContentPane) tab.getContent();
            if (cp.modeWebNavigation)
                continue;
            
            if (tab.isClosable())
                tabs.add(tab);
        }
        for (Tab tab : tabs)
        {
            tabpaneContent.getTabs().remove(tab);
        }
    }
    
    /**
     * Добавить новое сообщение в чат. Сообщение формируется в {@link ChatMessage}
     */
    public void addMessageToChat(MC_Message mc_message)
    {
        ChatMessage chatMessage = new ChatMessage(mc_message);
        
        // пишем в файл лога
        if (config.WriteMessagesToFile)
            FileFunc.saveTextToFile(LogChatFile, chatMessage.toString().replace("\n", ""), config.Encoding, true);
        
        // игнорируем сообщения от МТ
        if (!config.ShowMessagesFromMinecraftingTools && mc_message.source.equals(config.SourceIsMinecraftingTools))
            return;
        
        // игнорируем сообщени от серверного плагина
        if (!config.ShowMessagesFromServerPlugin && mc_message.type == MC_Message.FROM_PLUGIN)
            return;
        
        // пишем во вкладки
        for (Tab tab : tabpaneContent.getTabs())
        {
            ContentPane contentPane = (ContentPane) tab.getContent();
            
            // вкладка веб-навикации
            if (contentPane.filter == null)
                continue;
            
            // общая вкладка
            if (contentPane.filter.player == null && contentPane.filter.channel == null)
            {
                contentPane.addMessage(chatMessage);
                
                if (!tab.isSelected())
                    ((MyTab) tab).applyStyle(true);
                else
                    ((MyTab) tab).applyStyle(false);
            }
            
            // вкладка игроков
            else if (contentPane.filter.player != null && (contentPane.filter.player.id == mc_message.associated_user ||
                                                           contentPane.filter.player.id == mc_message.author_id ||
                                                           contentPane.filter.player.id == mc_message.opponent_id))
            {
                contentPane.addMessage(chatMessage);
                
                if (!tab.isSelected())
                    ((MyTab) tab).applyStyle(true);
                else
                    ((MyTab) tab).applyStyle(false);
            }
            
            // вкладка каналов
            else if (contentPane.filter.channel != null && (contentPane.filter.channel.id == mc_message.channel_id))
            {
                contentPane.addMessage(chatMessage);
                
                if (!tab.isSelected())
                    ((MyTab) tab).applyStyle(true);
                else
                    ((MyTab) tab).applyStyle(false);
            }
        }
        
        // поднимаю приложение при получении сообщения
        if (config.PopUpMainWindowAtMessage && ((Stage) getWindow()).isIconified())
            ((Stage) getWindow()).setIconified(false);
        
        // воспроизвожу звуковой сигнал нового сообщения
        if (config.PlaySoundAtMessage)
            new SoundPlayer(config.NotificationSoundFile).start();
    }
    
    /**
     * искат в текущем чате
     */
    private void searchOnCurrentChatTab()
    {
        ContentPane chatPane = (ContentPane) tabpaneContent.getSelectionModel().getSelectedItem().getContent();
        chatPane.updateSearchPane();
    }
    
    /**
     * Создать вкладку для канала
     */
    private void ChannelNewTabClick()
    {
        ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
        if (cle == null)
            return;
        
        for (Tab tab : tabpaneContent.getTabs())
        {
            ContentPane cp = (ContentPane) tab.getContent();
            
            if (cp.filter == null || cp.filter.channel == null)
                continue;
            
            if (cp.filter.channel.id == cle.id)
            {
                tabpaneContent.getTabs().remove(tab);
                return;
            }
        }
        
        addContentTab(cle.getName(), cle.image, true, new ContentFilter(cle, null));
    }
    
    /**
     * Создать вкладку для игрока
     */
    private void PlayerNewTabClick()
    {
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        if (ple == null)
            return;
        
        for (Tab tab : tabpaneContent.getTabs())
        {
            ContentPane cp = (ContentPane) tab.getContent();
            
            if (cp.filter == null || cp.filter.player == null)
                continue;
            
            if (cp.filter.player.id == ple.id)
            {
                tabpaneContent.getTabs().remove(tab);
                return;
            }
        }
        
        addContentTab(ple.getNick(), ple.image, true, new ContentFilter(null, ple));
    }
    
    /**
     * сохранить в файл содержание открытой вкладки чата
     */
    private void saveCurrentChatTab()
    {
        ContentPane chatPane = (ContentPane) tabpaneContent.getSelectionModel().getSelectedItem().getContent();
        
        String file = FileFunc.showSaveFileDialog(getWindow(),
                                                  "Сохранить документ",
                                                  new FileChooser.ExtensionFilter("документы HTML (*.html)", "*.html"),
                                                  new FileChooser.ExtensionFilter("текстовые документы (*.txt)",
                                                                                  "*.txt"));
        if (file.isEmpty())
            return;
        
        long result = 0;
        if (file.toLowerCase().endsWith(".txt"))
            result = chatPane.SaveAllToTXTFile(new File(file));
        else if (file.toLowerCase().endsWith(".html"))
            result = chatPane.SaveAllToHTMLFile(new File(file));
        
        if (result > -1)
        {
            ArrayList <ContentElement> message = new ArrayList <>();
            message.add(new ContentElement("Данные сохранены в файл: "));
            message.add(new ContentElement(file, new MyLink("file", TextFunc.SetPathToHTML(file)).get())
                                .addElementClass("info-message"));
            message.add(new ContentElement(String.format(", размер %1$s ",
                                                         TextFunc.humanReadableByteCount(result, true))));
            logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_INFO));
        }
        else if (result == -1)
            logContent.addMessage(new LogMessage(new ContentElement("Сохранить данные в файл не удалось"),
                                                 LogMessage.MESSAGE_ERROR));
    }
    
    /**
     * Панель отправки сообщений - нажать кнопку с выпадающим списком
     */
    private void OptionsSendMessageClick()
    {
        ContextMenu contextMenu = new ContextMenu();
        Menu presetsMenu = new Menu("Шаблоны");
        contextMenu.getItems().add(presetsMenu);
        Menu commandsMenu = new Menu("Команды");
        contextMenu.getItems().add(commandsMenu);
        
        // добавление списка команд в Команды
        // TODO добавить
        
        // добавление подменю с именами файлов в Шаблоны
        File cat = new File(PresetsDirectory);
        if (!cat.exists() || cat.isFile())
        {
            logContent.addMessage(new LogMessage(new ContentElement(String.format(
                    "Каталог шаблонов указан неверно: \"%1$s\"",
                    PresetsDirectory)), LogMessage.MESSAGE_ERROR));
            return;
        }
        
        try
        {
            File[] files = cat.listFiles();
            if (files == null || files.length == 0)
            {
                ArrayList <ContentElement> message = new ArrayList <>();
                message.add(new ContentElement("Каталог шаблонов пустой: "));
                message.add(new ContentElement(PresetsDirectory,
                                               new MyLink("file", TextFunc.SetPathToHTML(PresetsDirectory)).get())
                                    .addElementClass("error-message"));
                
                logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_ERROR));
                return;
            }
            
            Arrays.sort(files);
            
            for (File f : files)
            {
                if (f.isFile() && f.getName().endsWith(".txt"))
                {
                    MenuItem subItem = new MenuItem(f.getName());
                    subItem.setOnAction(lambda -> {
                        String path = PresetsDirectory + File.separator + subItem.getText();
                        File file = new File(path);
                        if (!file.exists() || file.isDirectory())
                        {
                            logContent.addMessage(new LogMessage(new ContentElement(String.format(
                                    "Не удалось найти файл: \"%1$s\"",
                                    path)), LogMessage.MESSAGE_ERROR));
                            return;
                        }
                        
                        String s = FileFunc.fileToString(path, config.Encoding);
                        
                        if (s == null)
                        {
                            ArrayList <ContentElement> message = new ArrayList <>();
                            message.add(new ContentElement("Не удалось прочесть файл: "));
                            message.add(new ContentElement(path, new MyLink("file", TextFunc.SetPathToHTML(path)).get())
                                                .addElementClass("error-message"));
                            logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_ERROR));
                            return;
                        }
                        textareaSendMessage.appendText(s);
                    });
                    presetsMenu.getItems().add(subItem);
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, String.format("Ошибка при работе с каталогом \"%1$s\"", PresetsDirectory), e);
        }
        
        contextMenu.show(buttonOptionsSendMessage, Side.BOTTOM, 0, 0);
    }
    
    /**
     * Отправить запрос по статистике игрока в MCRU
     */
    private void MCRU_Request()
    {
        if (listPlayers.getSelectionModel().getSelectedIndex() < 0)
            return;
        
        if (!isConnection(true))
            return;
        
        if (getStatusUserWhoIs())
        {
            logContent.addMessage(new LogMessage(String.format("%1$s сейчас занят, попробуйте позже", NAME),
                                                 LogMessage.MESSAGE_ERROR));
            return;
        }
        
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        
        if (connection.userWhoisID < 0)
        {
            logger.log(Level.SEVERE, String.format("Ошибка протокола, UserWhoisID = %1$d", connection.userWhoisID));
            return;
        }
        
        if (!ShowConfirmationDialog("Подтвердите",
                                    null,
                                    String.format("Отправить запрос в Minecrafring.ru об игроке %1$s?", ple.nick),
                                    null))
            return;
        
        // включаю индикацию progressBar
        setStatusUserWhoIs(true);
        
        logContent.addMessage(new LogMessage(String.format(
                "Отправлен запрос по игроку %1$s на сервер Minecrafting.ru...",
                ple.nick), LogMessage.MESSAGE_INFO));
        
        // отсылаем запрос по игроку
        PerformActionPacket packet = connection.clientEnvironmentManager
                .packAction(connection.userWhoisID, Collections.emptyMap(), (int) ple.id);
        connection.client.sendPacket(packet);
    }
    
    /**
     * запрос поиска игрока в MCRU
     */
    private void MCRU_Search()
    {
        if (!isConnection(true))
            return;
        
        if (getStatusUserFind())
        {
            logContent.addMessage(new LogMessage(String.format("%1$s сейчас занят, попробуйте позже", NAME),
                                                 LogMessage.MESSAGE_ERROR));
            return;
        }
        
        new MCRUSearchDialog();
    }
    
    /**
     * запрос по игроку в Mojang
     */
    private void Mojang_Request()
    {
        if (listPlayers.getSelectionModel().getSelectedIndex() < 0)
            return;
        
        if (!isConnection(true))
            return;
        
        if (getStatusMojangProfileWebSearcher())
        {
            logContent.addMessage(new LogMessage(String.format("%1$s сейчас занят, попробуйте позже", NAME),
                                                 LogMessage.MESSAGE_ERROR));
            return;
        }
        
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        
        if (connection.userWhoisID < 0)
        {
            logger.log(Level.SEVERE, String.format("Ошибка протокола, UserWhoisID = %1$d", connection.userWhoisID));
            return;
        }
        
        if (!ShowConfirmationDialog("Подтвердите",
                                    null,
                                    String.format("Отправить запрос в Mojang API\nоб игроке %1$s?", ple.nick),
                                    null))
            return;
        
        // включаю индикацию progressBar
        setStatusMojangProfileWebSearcher(true);
        
        logContent
                .addMessage(new LogMessage(String.format("Отправляем запрос об игроке %1$s в Mojang API...", ple.nick),
                                           LogMessage.MESSAGE_INFO));
        
        MojangProfileWebSearcher mpws = new MojangProfileWebSearcher(ple.uuid.replace("-", ""), ple.id);
        mpws.start();
    }
    
    /**
     * Отобразить информацию о канале
     */
    private void showInfoChannel()
    {
        ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
        if (cle == null)
            return;
        
        new InfoChannelDialog(cle);
    }
    
    /**
     * Отобразить информацию о игроке
     */
    private void showInfoPlayer()
    {
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        if (ple == null)
            return;
        
        new InfoPlayerDialog(ple);
    }
    
    /**
     * Обновить содержание списка каналов
     */
    public void updateCLE()
    {
        scene.updateChannelsTab();
        listChannels.refresh();
    }
    
    /**
     * Обновить содержание списка игроков
     */
    public void updatePLE()
    {
        scene.updatePlayersTab();
        listPlayers.refresh();
    }
    
    /**
     * Создать контекстное меню списка каналов
     */
    private ContextMenu createListChannelsContextMenu()
    {
        ContextMenu contextMenu = new ContextMenu();
        ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
        if (cle == null)
            return contextMenu;
        
        // копировать название группы в буфер
        MenuItem miCopyName = new MenuItem("Копировать название в буфер", new ImageView(ResFunc.getImage("copy16")));
        miCopyName.setOnAction(lambda -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(cle.name);
            clipboard.setContent(content);
        });
        
        contextMenu.getItems().addAll(miCopyName, new SeparatorMenuItem());
        
        // покинуть пати
        if (cle.type.equals("party"))
        {
            MenuItem miLeaveParty = new MenuItem(String.format("Покинуть группу \"%1$s\"", cle.name),
                                                 new ImageView(ResFunc.getImage("prev16")));
            miLeaveParty.setOnAction(lambda -> leaveParty(cle));
            contextMenu.getItems().add(miLeaveParty);
            
        }
        
        MenuItem miLeaveAllParty = new MenuItem("Покинуть все группы", new ImageView(ResFunc.getImage("prev16")));
        miLeaveAllParty.setOnAction(lambda -> leaveAllParties(true));
        contextMenu.getItems().add(miLeaveAllParty);
        
        // пригласить в группу
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        if (ple != null && cle.type.equals("party"))
        {
            MenuItem miInviteInParty =
                    new MenuItem(String.format("Пригласить в группу \"%1$s\" игрока %2$s", cle.name, ple.nick),
                                 new ImageView(ResFunc.getImage("party16")));
            miInviteInParty.setOnAction(lambda -> invitePlayerInParty(cle, ple));
            contextMenu.getItems().add(miInviteInParty);
        }
        
        return contextMenu;
    }
    
    /**
     * Создать контекстное меню списка игроков
     */
    private ContextMenu createListPlayersContextMenu()
    {
        ContextMenu contextMenu = new ContextMenu();
        PlayerListElement ple = listPlayers.getSelectionModel().getSelectedItem();
        if (ple == null)
            return contextMenu;
        
        // копировать ник
        MenuItem miCopyNick = new MenuItem("Копировать ник в буфер", new ImageView(ResFunc.getImage("copy16")));
        miCopyNick.setOnAction(lambda -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(ple.nick);
            clipboard.setContent(content);
        });
        
        // искать в чате
        MenuItem miSearchNick =
                new MenuItem("Искать в текущем чате\t\tCTRL+P", new ImageView(ResFunc.getImage("search16")));
        miSearchNick.setOnAction(lambda -> getCurrentChatContentPane().searchText(ple.nick));
        
        // создать группу
        MenuItem miCreateParty = new MenuItem(String.format("Создать группу с игроком %1$s", ple.nick),
                                              new ImageView(ResFunc.getImage("party16")));
        miCreateParty.setOnAction(lambda -> createPartyWithPlayer(ple));
        
        //удалить из БД Можанга
        MenuItem miCDeleteFromMojang =
                new MenuItem("Удалить из БД Mojang", new ImageView(ResFunc.getImage("delete16")));
        miCDeleteFromMojang.setOnAction(lambda -> {
            if (CommonDialogs.ShowConfirmationDialog("Подтвердите",
                                                     null,
                                                     String.format(
                                                             "Удалить данные об игроке \"%s\" из локальной БД Mojang?",
                                                             ple.nick),
                                                     null))
                ApplicationControl.mojangProfiles.remove(ple.uuid);
        });
        
        //удалить из БД MCRU
        MenuItem miCDeleteFromMCRU =
                new MenuItem("Удалить из БД Minecrafting", new ImageView(ResFunc.getImage("delete16")));
        miCDeleteFromMCRU.setOnAction(lambda -> {
            if (CommonDialogs.ShowConfirmationDialog("Подтвердите",
                                                     null,
                                                     String.format(
                                                             "Удалить данные об игроке \"%s\" из локальной БД Minecrafting?",
                                                             ple.nick),
                                                     null))
            {
                ApplicationControl.minecraftingProfiles.remove(ple.uuid);
                updatePLE();
            }
        });
        
        contextMenu.getItems().addAll(miCopyNick,
                                      miSearchNick,
                                      miCreateParty,
                                      new SeparatorMenuItem(),
                                      miCDeleteFromMojang,
                                      miCDeleteFromMCRU);
        
        // пригласить в группу
        ChannelListElement cle = listChannels.getSelectionModel().getSelectedItem();
        if (cle != null && cle.type.equals("party"))
        {
            MenuItem miInviteInParty =
                    new MenuItem(String.format("Пригласить в группу \"%1$s\" игрока %2$s", cle.name, ple.nick),
                                 new ImageView(ResFunc.getImage("party16")));
            miInviteInParty.setOnAction(lambda -> invitePlayerInParty(cle, ple));
            contextMenu.getItems().add(miInviteInParty);
        }
        
        return contextMenu;
    }
    
    /**
     * Создание группы с игроком
     */
    private void createPartyWithPlayer(PlayerListElement ple)
    {
        if (!isConnection(true))
            return;
        
        if (connection.partyInviteID < 0)
            return;
        
        if (ple == null || ple.id < 0)
            return;
        
        if (!CommonDialogs.ShowConfirmationDialog("Подтвердите",
                                                  null,
                                                  String.format("Создать новую группу с игроком %1$s?", ple.nick),
                                                  null))
            return;
        
        
        logger.log(Level.INFO, String.format("Создаём новую группу с игроком %1$s", ple.nick));
        PerformActionPacket packet = connection.clientEnvironmentManager
                .packAction(connection.partyInviteID, Collections.emptyMap(), (int) ple.id);
        connection.client.sendPacket(packet);
    }
    
    /**
     * пригласить выбранного игрока в выбранную группу
     */
    private void invitePlayerInParty(ChannelListElement cle, PlayerListElement ple)
    {
        if (!isConnection(true))
            return;
        
        if (connection.partyInviteID < 0)
            return;
        
        if (ple == null || ple.id < 0 || cle == null || cle.id < 0)
            return;
        
        if (!CommonDialogs.ShowConfirmationDialog("Подтвердите",
                                                  null,
                                                  String.format("Пригласить игрока %1$s в группу \"%2$s\"?",
                                                                ple.nick,
                                                                cle.name),
                                                  null))
            return;
        
        logger.log(Level.INFO, String.format("Приглашаем игрока %1$s в группу \"%2$s\"", ple.nick, cle.name));
        PerformActionPacket packet = connection.clientEnvironmentManager
                .packAction(connection.partyInviteID, Collections.singletonMap("channel", (int) cle.id), (int) ple.id);
        connection.client.sendPacket(packet);
    }
    
    /**
     * Покинуть все группы
     */
    private void leaveAllParties(boolean toConfirm)
    {
        if (!isConnection(true))
            return;
        
        if (connection.partyLeaveID < 0)
            return;
        
        if (toConfirm)
        {
            if (!CommonDialogs.ShowConfirmationDialog("Подтвердите", null, "Покинуть все группы?", null))
                return;
        }
        
        logger.log(Level.INFO, "Покидаем все группы");
        PerformActionPacket packet =
                connection.clientEnvironmentManager.packAction(connection.partyLeaveID, Collections.emptyMap());
        
        connection.client.sendPacket(packet);
    }
    
    /**
     * покинуть указанную группу
     */
    private void leaveParty(ChannelListElement cle)
    {
        if (!isConnection(true))
            return;
        
        if (connection.partyLeaveID < 0)
            return;
        
        if (cle.id < 0)
            return;
        
        if (!CommonDialogs.ShowConfirmationDialog("Подтвердите",
                                                  null,
                                                  String.format("Покинуть группу \"%1$s\"?", cle.name),
                                                  null))
            return;
        
        PerformActionPacket packet = connection.clientEnvironmentManager
                .packAction(connection.partyLeaveID, Collections.singletonMap("channel", (int) cle.id));
        
        connection.client.sendPacket(packet);
    }
    
    /**
     * Проверить связь с сервером для дальнейшего взаимодействия с севером
     */
    public boolean isConnection(boolean writeMessage)
    {
        if (connection == null || connection.client == null || !connection.client.isActive())
        {
            if (writeMessage)
                logContent.addMessage(new LogMessage("Нет связи с сервером", LogMessage.MESSAGE_ERROR));
            
            return false;
        }
        return true;
    }
    
    /**
     * отобразить диалог настройки аутентификаций
     */
    private void AuthentificationSetup()
    {
        new EditAuthentificationDialog(getWindow());
    }
    
    /**
     * Проверить статусы серверов Mojang
     */
    private void checkMojangServerStatus()
    {
        if (getStatusMojangServerStatusChecker())
        {
            logContent.addMessage(new LogMessage(String.format("%1$s сейчас занят, попробуйте позже", NAME),
                                                 LogMessage.MESSAGE_ERROR));
            return;
        }
        
        setStatusMojangServerStatusChecker(true);
        new MojangServerStatusChecker().start();
    }
    
    /**
     * вывести информацию о вводимом тексте
     */
    private void setTextMessageStatus(String text)
    {
        Platform.runLater(() -> labelTextMessageStatus.setText(String.format("%d знаков", text.length())));
    }
    
    /**
     * Просмотр локальной БД Можанг
     */
    private void showLocalDBMojang()
    {
        MojangDBListBuilder task = new MojangDBListBuilder();
        new WebViewInfoDialog(getWindow(), "Локальная БД Mojang", task).startTask();
    }
    
    /**
     * Просмотр локальной БД Minecrafting
     */
    private void showLocalDBMinecrafting()
    {
        MinecraftingDBListBuilder task = new MinecraftingDBListBuilder();
        new WebViewInfoDialog(getWindow(), "Локальная БД Minecrafting", task).startTask();
    }
    
    /**
     * открыть файл с логом чата
     */
    private void openLogChat()
    {
        File lfc = new File(LogChatFile);
        if (lfc.exists() && lfc.isFile())
            DesktopFunc.exploreFile(lfc);
        else
        {
            if (config.WriteMessagesToFile)
                logContent.addMessage(new LogMessage(String.format("Файл не найден: \"%s\"", LogChatFile),
                                                     LogMessage.MESSAGE_ERROR));
            else
                logContent.addMessage(new LogMessage("Запись лога чата в файл отключена в настройках",
                                                     LogMessage.MESSAGE_ERROR));
        }
    }
    
    /**
     * получить текущий {@link ContentPane} с чатом
     */
    public ContentPane getCurrentChatContentPane()
    {
        for (Tab tab : tabpaneContent.getTabs())
        {
            ContentPane cp = (ContentPane) tab.getContent();
            
            if (cp.modeWebNavigation)
                continue;
            
            if (tab.isSelected())
                return cp;
        }
        return null;
    }
    
    /**
     * создать расширенную подсказку для канала
     */
    public String getChannelFoolToolTip(long id)
    {
        StringBuilder text = new StringBuilder();
        ChannelListElement cle = scene.getChannelListElementByID(id);
        if (cle == null)
            return "";
        
        text.append(cle.toString().replace(" / ", "\n")).append("\nНа канале присутствуют:");
        for (long pid : cle.playersID)
        {
            PlayerListElement ple = scene.getPlayerListElementByID(pid);
            if (ple == null)
                continue;
            text.append("\n").append(ple.getNick());
        }
        
        return text.toString();
    }
    
    /**
     * создать расширенную подсказку для игрока
     */
    public String getPlayerFoolToolTip(long id)
    {
        StringBuilder text = new StringBuilder();
        
        PlayerListElement ple = scene.getPlayerListElementByID(id);
        if (ple == null)
            return "";
        
        text.append(ple.toString().replace(" / ", "\n\n"));
        
        text.append(String.format("online_flags:\t%s\n", DecodePlayerOnlineFlags(ple.online_flags)));
        text.append(String.format("status_flags:\t%s\n", DecodePlayerStatusFlags(ple.status_flags)));
        
        MinecraftingProfile mp = minecraftingProfiles.find(ple.uuid);
        String violation = "\nНет правонарушений\n\n";
        if (mp != null)
        {
            if (!mp.SanctionsList.isEmpty())
                violation = String.format("\nПравонарушений:\t%1$d\n\n", mp.SanctionsList.size());
        }
        else
            violation = "\nПравонарушений:\t?\n\n";
        
        text.append(violation);
        
        for (Map.Entry <String, DataValue> ple_entry : ple.getProperties().entrySet())
        {
            if (ple_entry.getKey().equals("played_total"))
                text.append(String.format("played_total:\t%s\n",
                                          TimeIntervalToString(ple_entry.getValue().asLong(), false)));
            if (ple_entry.getKey().equals("played_server"))
                text.append(String.format("played_server:\t%s\n",
                                          TimeIntervalToString(ple_entry.getValue().asLong(), false)));
        }
        
        return text.toString();
    }
}
