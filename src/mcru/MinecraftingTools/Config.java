package mcru.MinecraftingTools;

import mcru.MinecraftingTools.Functions.SysFunc;
import mcru.MinecraftingTools.Helpers.HighlightElement;

import java.util.ArrayList;

/**
 * класс для хранения всех настроек приложения
 */
public class Config
{
    // основное окно всегда поверх остальных окон
    public boolean AlwaysOnTop = false;
    // запоминать пароль при аутентификации
    public boolean RememberPassword = false;
    // показывать дату сообщения в чате
    public boolean ShowChatMessageDate = true;
    // показывать источник сообщения в чате
    public boolean ShowChatMessageSource = true;
    // показывать канал в сообщениях чата
    public boolean ShowChatMessageChannel = true;
    // поднимать основное окно из панели задач при получении сообщений в чате
    public boolean PopUpMainWindowAtMessage = true;
    // воспроизводить звуковой сигнал при получении сообщений в чате
    public boolean PlaySoundAtMessage = false;
    // записывать все сообщения чата сразу в файл (вести лог чата),
    // имя файла генерится при подключении и отображается в панели сообщений
    public boolean WriteMessagesToFile = false;
    // покидать все группы (в чате) при выходе с сервера
    public boolean LeaveAllPartiesAtDisconnect = true;
    // отображать диалог подключения к серверу при старте приложения
    public boolean ShowAccessDialogAtStart = false;
    // отображать в панели чата кнопки информации о сообщении
    public boolean ShowIconsInChat = true;
    // Звуковое оповещение при разрыве связи с сервером
    public boolean PlayAlarmDisconnectSound = false;
    // распознавать URL`ы в сообщениях чата
    public boolean RecognizeURLInMessage = true;
    // добавлять к ссылкам DefaultUrlProtocol, если он не указан явно
    public boolean ToAddDefaultUrlProtocol = true;
    // писать во вкладках чата размер документа
    public boolean WriteContentSizeInTabs = false;
    // показывать дополнительный функционал
    public boolean ShowAdditionalFunctional = true;
    // показывать сообщения от МТ
    public boolean ShowMessagesFromMinecraftingTools = true;
    // показывать сообщения от серверного плагина
    public boolean ShowMessagesFromServerPlugin = true;
    // писать в чате номера сообщений
    public boolean WriteMessageNumbers = true;
    // использовать CSS
    public boolean UseCSS = true;
    // показывать метку на игроках о наличии правонарушений
    public boolean ShowPlayerViolationMark = true;
    // разрешать использовать колёсики мышки для масштабирования
    public boolean MouseWealZooming = true;
    //сохранять текстовые документы в формате для Windows
    public boolean ToSaveTextsForWindowsOS = false;
    // выполнять скрипт по обновлению сообщений при получении нового сообщения
    public boolean RepaintNodesAtNewMessage = true;
    // использовать SHIFT+ENTER для перехода на новую строку
    public boolean ShiftEnterAsNewLine = false;
    // пытаться восстановить связь после потери связи
    public boolean TryToReconnected = false;
    // ширина основного окна
    public double MainWindowWidth = 1024.0;
    // высота основного окна
    public double MainWindowHeight = 800.0;
    // ширина информационого окна
    public double InfoWindowWidth = 600.0;
    // высота информационого окна
    public double InfoWindowHeight = 600.0;
    // ширина окна настроек
    public double ConfigWindowWidth = 900.0;
    // ширина окна настроек
    public double ConfigWindowHeight = 850.0;
    // размер шрифта во всём приложении
    public double CommonFontSize = 16;
    // масштаб увеличения метки адресата в панели отправки сообщений
    public double SendMessageLabelScale = 1.5;
    // позиция горизонтального разделителя - между панелью основного содержания и панелью сообщений
    public double[] MainSplitPaneLocation = {0.85};
    // позиция вертекального разделителя основного содержания
    public double[] ContentSplitPaneLocation = {0.3};
    // позиция горизонтального разделителя  - между панелью чата и панелью отправки сообщений
    public double[] MessageSplitPaneLocation = {0.8};
    // число попыток восстановить связ после потери связи
    public int CountTryReconnection = 3;
    // максимально допустимая длина заголовка вкладок
    public int ContentTabCaptionLength = 15;
    // максимально допустимая длина строки в списках игроков и каналов
    public int ChannelPlayerListLength = 20;
    // длина в символах (пробелах) компонента ComboBox (по необходимости)
    public int ComboBoxPlaceHolderLength = 60;
    // размер истории поиска
    public int SearchHistorySize = 50;
    // время в милисекундах на показ сплеш-окна
    public int SplashScreenTime = 500;
    // масштаб увеличения шкурок игроков при просмотре данных Mojang API
    public int SkinViewerScale = 4;
    // временной интервал в милисекундах между проверками состояния связи с сервером (для StatusChecker)
    public int ServerAccessCheckInterval = 500;
    // длина токена при регистрации на сервере (до 16, сейчас сервер поддерживает не более 8)
    public int ServerTokenLength = 8;
    // если игрок отыграл на сервере менее указанного срока (в часах), то будет отображаться метка
    public int PlayedServerAlertValue = 24;
    // максимальный объём чата в количестве сообщений
    public int ChatMaximumMessagesCount = 9999;
    // жирнота шрифта в приложении
    public int CommonFontWeight = 7;
    // имя шрифта в приложении
    public String WebViewFontFamily = "";
    // глубина логирования
    public String LoggingLevel = "ALL";
    // протокол по-умолчанию для подстановки в URL`ы без явно указанного протокола (для открывания браузером)
    public String DefaultUrlProtocol = "http://";
    // открывающая скобка для автора сообщения
    public String OpeningBraceForMessageAuthor = "<";
    // закрывающая скобка для автора сообщения
    public String ClosingBraceForMessageAuthor = ">";
    // открывающая скобка для ассоциированного игрока в сообщении
    public String OpeningBraceForMessageAssPlayer = "<<";
    // закрывающая скобка для ассоциированного игрока в сообщении
    public String ClosingBraceForMessageAssPlayer = ">>";
    // открывающая скобка для даты-времени сообщения
    public String OpeningBraceForMessageDateTime = "[";
    // закрывающая скобка для даты-времени сообщения
    public String ClosingBraceForMessageDateTime = "]";
    // открывающая скобка для источника сообщения
    public String OpeningBraceForMessageSource = "{";
    // закрывающая скобка для источника сообщения
    public String ClosingBraceForMessageSource = "}";
    // открывающая скобка для канала в сообщении
    public String OpeningBraceForMessageChannel = "|";
    // закрывающая скобка для канала в сообщении
    public String ClosingBraceForMessageChannel = "|";
    // разделитель для адресата и автора в личных сообщениях
    public String PMessageAuthorOpponentSeparator = "&";
    // символ для обозначения входящего сообщения
    public String PMessageIncomingSymbol = "@";
    //  обозначение сообещний от МТ
    public String SourceIsMinecraftingTools = "MT";
    // обозначение личных сообщений
    public String ChannelIsPersonalMessage = "ЛС";
    // рабочая кодировка для всех текстов (используется много где, лучше не менять)
    public String Encoding = "UTF-8";
    // имя файла справки (в каталоге help)
    public String HelpFileName = "help.txt";
    // базовый стиль
    public String CommonCSS = "MT_COLOR: #%1$s; MT_COLOR_DARK: #%2$s; MT_COLOR_LIGHT: #%3$s;" +
                              "-fx-font-size: %4$s; -fx-font-family: monospace;" +
                              "-fx-selection-bar-non-focused: -fx-selection-bar;";
    // путь до внешнего файла CSS
    public String CSSFilePath = "";
    // шаблон URL`а для получения данных о истории ника из Mojang API
    public String MojangApiNameHistory = "https://api.mojang.com/user/profiles/%1$s/names";
    // шаблон URL`а для получения данных о профиле игрока из Mojang API 
    public String MojangApiProfile = "https://sessionserver.mojang.com/session/minecraft/profile/%1$s";
    // URL для получения статусов серверов Mojang 
    public String MojangApiServerStatus = "https://status.mojang.com/check";
    // шаблон даты-времени для панели сообщений
    public String LogDateTimeFormat = "HH:mm:ss";
    // шаблон времени для панели чата
    public String MessageTimeFormat = "HH:mm:ss";
    // шаблон даты для панели чата
    public String MessageDateFormat = "yyyy.MM.dd";
    // шаблон даты-времени для для всех остальных случаев
    public String CommonDateTimeFormat = "yyyy.MM.dd HH:mm:ss";
    // звуковой файл для уведомлений о событиях чата
    public String NotificationSoundFile = "";
    // звуковой файл для уведомления о потере связи с сервером
    public String AlarmDisconnectSoundFile = "";
    // временная зона
    public String TimeZoneOffset = "+03:00";
    // регексп распознавания URL`ов
    public String URLRegexp = "(https?|http|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    // цвет основной темы приложения
    public String ThemeWebColor = "E7E7E7";
    // цвет отображения даты и времени
    public String CommonDataTimeWebColor = "979797";
    // цвет для отображения даты-времени в сообщениях и чате
    public String ChatMessageSourceWebColor = "800000";
    // цвет для отображения автора сообщений в чате
    public String ChatMessageAuthorWebColor = "000080";
    // цвет для отображения сообщений чата
    public String ChatMessageMessageWebColor = "000000";
    // цвет для отображения канала в чате
    public String ChatMessageChannelWebColor = "008000";
    // цвет для отображения сообщений об ошибках в панели сообщений
    public String EventErrorWebColor = "800000";
    // цвет для отображения информационных сообщений в панели сообщений
    public String EventInfoWebColor = "000080";
    // цвет для отображения сообщений об удачном исходе в панели сообщений
    public String EventSeccessWebColor = "008000";
    // цвет для отображения прочих сообщений в панели сообщений
    public String EventOtherWebColor = "000000";
    // цвет для подсветки вкладки с новым сообщением
    public String NewMessageTabWebColor = "FFD700";
    // история поиска по чату
    public ArrayList <String> SearchHistory = new ArrayList <>();
    //история веб-навигации
    public ArrayList <String> WebNavigationHistory = new ArrayList <>();
    // история поиска игрока по нику на MCRU
    public ArrayList <String> MCRUUserFindNickHistory = new ArrayList <>();
    // история поиска игрока по UUID на MCRU
    public ArrayList <String> MCRUUserFindUuidHistory = new ArrayList <>();
    // список подсветок сообщений в чате
    public ArrayList <HighlightElement> MessageHighlightsList = new ArrayList <>();
    // список подсветок каналов в чате
    public ArrayList <HighlightElement> ChannelHighlightsList = new ArrayList <>();
    // список подсветок источников в чате
    public ArrayList <HighlightElement> SourceHighlightsList = new ArrayList <>();
    
    /**
     * дефолтный конструктор
     */
    public Config()
    {
        ChannelHighlightsList.add(new HighlightElement("Канал администрации", "^#adm$", true, "B0E0E6"));
        ChannelHighlightsList.add(new HighlightElement("Пати", "^#party.*$", true, "F0E68C"));
        ChannelHighlightsList.add(new HighlightElement("Личные сообщения", "^ЛС$", true, "D8BFD8"));
        SourceHighlightsList.add(new HighlightElement("Minecrafting Tools", "^MT$", false, "DCDCDC"));
        SourceHighlightsList.add(new HighlightElement("Плагин", "^plugin$", false, "DCDCDC"));
        MessageHighlightsList.add(new HighlightElement("Сообщение \"!\"", "^!.*$", true, "FFA07A"));
        MessageHighlightsList.add(new HighlightElement("Сообщение \"впервые вошел на сервер\"",
                                                       "^.*впервые вошел на сервер.*$",
                                                       true,
                                                       "FFA07A"));
        
        WebViewFontFamily = SysFunc.getDefaultFontName();
    }
}

// https://colorscheme.ru/html-colors.html
