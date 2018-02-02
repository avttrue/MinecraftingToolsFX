package mcru.MinecraftingTools.Interface;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import mcru.MinecraftingTools.ApplicationControl;
import mcru.MinecraftingTools.Dialogs.CommonDialogs;
import mcru.MinecraftingTools.Functions.FileFunc;
import mcru.MinecraftingTools.Functions.ImgFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.*;
import mcru.MinecraftingTools.MyApplication;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Functions.ResFunc.getScript;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Панель для вывода форматированного текста
 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/web/WebEngine.html
 */
public class ContentPane extends BorderPane
{
    /**
     * шаблон документа
     */
    private static final String DocumentTemplate =
            "<!DOCTYPE html><HTML><HEAD><TITLE>%1$s</TITLE>\n<META content=\"text/html\">\n<META charset=\"%2$s\">\n" +
            "<STYLE>%3$s</STYLE>\n</HEAD><BODY STYLE=\"%4$s\">\n%5$s</BODY></HTML>";
    /**
     * шаблон стиля всего документа
     */
    private static final String HeadStyleTemplate =
            "\nSPAN {word-wrap: break-word; font-size: %1$dpx; font-family: '%2$s', monospace; font-weight:%3$d;}\n" +
            "A {word-wrap: break-word; font-size: %1$dpx; font-family: '%2$s', monospace; font-weight:%3$d; text-decoration: none; border-bottom: 1px dashed;}\n" +
            "KBD {font-size: small; font-family: '%2$s', monospace; font-weight: bold; text-align: center; font-color:#%4$s;}\n" +
            ".new-line {background-repeat: no-repeat; background-position: left center; padding-left: 25px;}\n" +
            ".image-left {background-repeat: no-repeat; background-position: left center; padding-left: 20px;}\n" +
            ".date-time {color: #%5$s;}\n.source {color: #%6$s;}\n" + ".channel {color: #%7$s;}\n" +
            ".player {color: #%8$s;}\n" +
            ".message {color: #%9$s;}\n.error-message {color: #%10$s;}\n.info-message {color: #%11$s;}\n" +
            ".success-message {color: #%12$s;}\n" +
            ".border {padding-bottom: 5px; margin: 5px; box-shadow: 0 0 5px rgba(0,0,0,0.5);}\n" +
            ".bubble16 {background-image: url('data:imageKey/png;base64,%13$s');}\n" +
            ".info16 {background-image: url('data:imageKey/png;base64,%14$s');}\n" +
            ".next16 {background-image: url('data:imageKey/png;base64,%15$s');}\n" +
            ".prev16 {background-image: url('data:imageKey/png;base64,%16$s');}\n" +
            ".service16 {background-image: url('data:imageKey/png;base64,%17$s');}\n" +
            ".chat16 {background-image: url('data:imageKey/png;base64,%18$s');}\n" +
            ".red_bookmark16 {background-image: url('data:imageKey/png;base64,%19$s');}\n" +
            ".OK16 {background-image: url('data:imageKey/png;base64,%20$s');}\n" +
            ".error16 {background-image: url('data:imageKey/png;base64,%21$s');}\n";
    
    /**
     * шаблон стиля всего BODY документа
     */
    private static final String BodyStyleTemplate = "background-color:#%1$s;";
    
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    public ContentFilter filter = new ContentFilter(null, null);
    public boolean modeWebNavigation = false;
    private boolean autoScroll = true;
    /**
     * готовность для приёма сообщений - лочит на момент загрузки шаблона или при перезагрузке содержания
     */
    private boolean ready = false;
    private int linesCount = 0;
    private int searchIndex = -1;
    
    private String title;
    private String charset;
    private WebView content;
    private HBox searchPane = new HBox();
    private HBox webnavPane = new HBox();
    private ComboBox <String> textFieldSearch = new ComboBox <>();
    private Object parent;
    private ContextMenu contextMenu = new ContextMenu();
    private ProgressBar progressBar = new ProgressBar();
    private ComboBox <String> textWebNav = new ComboBox <>();
    private Map <String, String> scripts = new HashMap <>();
    
    private JSBridge jsBridge = new JSBridge();
    
    /**
     * Конструктор панели для отображения контента
     * @param title             заголовок документа
     * @param charset           кодировка
     * @param parent            родитель, используется в {@link ContentPane#updateParent()}
     * @param modeWebNavigation режим веб-навигации да-нет
     * @param startContent      сообщения, которые будут отображены после создания панели (в режиме веб-навигации -
     *                          адрес страницы)
     */
    public ContentPane(String title, String charset, Object parent, boolean modeWebNavigation, String startContent)
    {
        super();
        this.title = title;
        this.charset = charset;
        this.parent = parent;
        this.modeWebNavigation = modeWebNavigation;
        
        // буферизирую скрипты
        scripts.put("script_get_html", getScript("script_get_html"));
        scripts.put("script_get_selection", getScript("script_get_selection"));
        scripts.put("script_repaint_all", getScript("script_repaint_all"));
        scripts.put("script_repaint_by_flag", getScript("script_repaint_by_flag"));
        scripts.put("script_repaint_message", getScript("script_repaint_message"));
        scripts.put("script_scroll", getScript("script_scroll"));
        scripts.put("script_search_down", getScript("script_search_down"));
        scripts.put("script_search_up", getScript("script_search_up"));
        
        content = new WebView();
        content.setFocusTraversable(false);
        setCenter(content);
        setFocusTraversable(false);
        setBorder(new Border(new MyBorder(5, 1)));
        
        // прогрессбар для загрузки веб-контента
        Worker <Void> worker = content.getEngine().getLoadWorker();
        worker.stateProperty().
                addListener((observable, oldValue, newValue) -> {
                    if (!modeWebNavigation)
                        return;
                    
                    setTop(progressBar);
                    
                    if (parent instanceof Tab)
                        ((Tab) parent).setText("Загрузка");
                    
                    if (newValue == Worker.State.SUCCEEDED || newValue == Worker.State.FAILED)
                    {
                        if (parent instanceof Tab)
                        {
                            ((Tab) parent).setTooltip(new Tooltip(content.getEngine().getLocation()));
                            
                            ((Tab) parent).setText(TextFunc.SetFixedSize(content.getEngine().getTitle(),
                                                                         config.ContentTabCaptionLength));
                        }
                        getChildren().remove(progressBar);
                    }
                });
        progressBar.progressProperty().bind(worker.progressProperty());
        progressBar.setPrefSize(Integer.MAX_VALUE, 5.0);
        progressBar.setMaxHeight(5.0);
        
        content.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.RUNNING)
                ready = false;
            
            else if (newState == Worker.State.SUCCEEDED)
            {
                registerJSBridge();
                ready = true;
            }
        });
        
        
        if (parent instanceof Tab)
            ((Tab) parent).setTooltip(new Tooltip(title));
        
        // отмена DnD
        content.setOnDragEntered(Event::consume);
        content.setOnDragOver(Event::consume);
        content.setOnDragExited(Event::consume);
        content.setOnDragDropped(Event::consume);
        content.setOnDragDone(Event::consume);
        //content.setOnDragDetected(Event::consume); // отменяет выделение мышью
        
        // контекстное меню (и отключаю встроенное)
        content.setContextMenuEnabled(false);
        content.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
            {
                contextMenu.hide();
                contextMenu = CreateContextMenu();
                contextMenu.show(content, e.getScreenX(), e.getScreenY());
            }
            else
                contextMenu.hide();
        });
        
        // панель поиска
        searchPane.setBorder(new Border(new MyBorder(5, 1)));
        
        Button closeSearch = new Button("", new ImageView(ResFunc.getImage("cancel16")));
        closeSearch.setTooltip(new Tooltip("Скрыть панель поиска"));
        closeSearch.setOnAction(lambda -> {
            getChildren().remove(searchPane);
            searchIndex = -1;
        });
        
        textFieldSearch.setPrefWidth(Integer.MAX_VALUE);
        textFieldSearch.setEditable(true);
        
        Button upSearch = new Button("", new ImageView(ResFunc.getImage("up16")));
        upSearch.setTooltip(new Tooltip("Искать выше"));
        upSearch.setOnAction(lambda -> {
            content.requestFocus();
            searchText(textFieldSearch.getEditor().getText(), searchIndex, "script_search_up");
        });
        
        Button downSearch = new Button("", new ImageView(ResFunc.getImage("down16")));
        downSearch.setTooltip(new Tooltip("Искать ниже"));
        downSearch.setOnAction(lambda -> {
            content.requestFocus();
            searchText(textFieldSearch.getEditor().getText(), searchIndex, "script_search_down");
        });
        
        searchPane.getChildren().addAll(closeSearch, textFieldSearch, upSearch, downSearch);
        
        // панель веб-навигации
        webnavPane.setBorder(new Border(new MyBorder(5, 1)));
        Button closeWebnav = new Button("", new ImageView(ResFunc.getImage("cancel16")));
        closeWebnav.setTooltip(new Tooltip("Скрыть панель навигации"));
        closeWebnav.setFocusTraversable(false);
        closeWebnav.setOnAction(lambda -> getChildren().remove(webnavPane));
        
        Button acceptWebnav = new Button("", new ImageView(ResFunc.getImage("OK16")));
        acceptWebnav.setTooltip(new Tooltip("Перейти по адресу"));
        acceptWebnav.setFocusTraversable(false);
        acceptWebnav.setOnAction(lambda -> {
            String text = textWebNav.getEditor().getText();
            if (text.isEmpty())
                return;
            openWebAddress(text);
        });
        
        textWebNav.setPrefWidth(Integer.MAX_VALUE);
        textWebNav.setEditable(true);
        textWebNav.setFocusTraversable(true);
        textWebNav.getItems().setAll(config.WebNavigationHistory);
        webnavPane.getChildren().addAll(closeWebnav, textWebNav, acceptWebnav);
        
        if (modeWebNavigation)
        {
            setTop(webnavPane);
            textWebNav.requestFocus();
        }
        
        // создаю документ
        if (!modeWebNavigation)
            createNewDocument(title, charset, startContent);
        else
            openWebAddress(startContent);
    }
    
    /**
     * Создание контекстного меню
     */
    private ContextMenu CreateContextMenu()
    {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem clearMenuItem = new MenuItem("Очистить", new ImageView(ResFunc.getImage("clear16")));
        clearMenuItem.setOnAction(lambda -> createNewDocument(title, charset, ""));
        
        MenuItem copyMenuItem = new MenuItem("Копировать", new ImageView(ResFunc.getImage("copy16")));
        copyMenuItem.setOnAction(lambda -> {
            String selection = (String) executeScript("script_get_selection");
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(selection);
            clipboard.setContent(content);
        });
        
        MenuItem copyallMenuItem = new MenuItem("Копировать всё", new ImageView(ResFunc.getImage("copy16")));
        copyallMenuItem.setOnAction(lambda -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(getDocumentText());
            clipboard.setContent(content);
        });
        
        MenuItem searchMenuItem = new MenuItem("Панель поиска\t\tCTRL+F", new ImageView(ResFunc.getImage("search16")));
        searchMenuItem.setOnAction(lambda -> updateSearchPane());
        
        CheckMenuItem autoscrollMenuItem =
                new CheckMenuItem("Автоперемотка", new ImageView(ResFunc.getImage("down16")));
        autoscrollMenuItem.setSelected(autoScroll);
        autoscrollMenuItem.setOnAction(lambda -> {
            setAutoScroll(autoscrollMenuItem.isSelected());
            if (autoScroll)
                executeScript("script_scroll");
        });
        
        MenuItem reloadMenuItem = new MenuItem("Перезагрузить документ", new ImageView(ResFunc.getImage("update16")));
        reloadMenuItem.setOnAction(lambda -> reloadContent());
        
        MenuItem repaintMenuItem = new MenuItem("Обновить\t\tCTRL+R", new ImageView(ResFunc.getImage("update16")));
        repaintMenuItem.setOnAction(lambda -> repaintAllContent());
        
        MenuItem webnavMenuItem =
                new MenuItem("Показать панель веб-навигации", new ImageView(ResFunc.getImage("www16")));
        webnavMenuItem.setOnAction(lambda -> showWebNavigationPane());
        
        // расширенный функционал
        MenuItem scriptMenuItem = new MenuItem("Выполнить JS", new ImageView(ResFunc.getImage("script16")));
        scriptMenuItem.setOnAction(lambda -> ExecuteScriptFromFile());
        
        MenuItem infoMenuItem = new MenuItem("Код документа", new ImageView(ResFunc.getImage("info16")));
        infoMenuItem.setOnAction(lambda -> CommonDialogs.ShowLongMessage(Alert.AlertType.INFORMATION,
                                                                         "Содержание",
                                                                         "Исходный код документа",
                                                                         (String) executeScript("script_get_html")));
        
        //основной функционал
        contextMenu.getItems().addAll(clearMenuItem, new SeparatorMenuItem(), copyMenuItem, copyallMenuItem);
        
        if (modeWebNavigation)
            contextMenu.getItems().addAll(new SeparatorMenuItem(), webnavMenuItem);
        else // в режиме веб-навикации не используем
            contextMenu.getItems().addAll(new SeparatorMenuItem(), autoscrollMenuItem, searchMenuItem, repaintMenuItem);
        
        //основной функционал
        contextMenu.getItems().addAll(new SeparatorMenuItem(), reloadMenuItem);
        
        //дополнительный функционал
        if (config.ShowAdditionalFunctional)
            contextMenu.getItems().addAll(new SeparatorMenuItem(), scriptMenuItem, infoMenuItem);
        
        return contextMenu;
    }
    
    /**
     * Создать новый документ
     * @param title   название документа
     * @param charset кодировка документа
     */
    private void createNewDocument(String title, String charset, String body)
    {
        WebEngine webengine = content.getEngine();
        
        String corrTitle = title.isEmpty() ? "" : String
                .format("%1$s (%2$s)", title, TextFunc.DateTimeToString(new Date().getTime()));
        
        webengine.loadContent(String.format(DocumentTemplate,
                                            corrTitle,
                                            charset,
                                            createHeadStyle(),
                                            createBodyStyle(),
                                            body));
        linesCount = 0;
        
        if (parent instanceof Tab)
        {
            String tabCaption = TextFunc.SetFixedSize(title, config.ContentTabCaptionLength);
            ((Tab) parent).setText(tabCaption);
        }
    }
    
    /**
     * Добавить к чату новое сообщение
     */
    public void addMessage(ChatMessage cm)
    {
        if (cm.get().isEmpty())
            return;
        
        if (!ready)
        {
            logger.log(Level.SEVERE, "WebView занят");
            return;
        }
        
        Document doc = content.getEngine().getDocument();
        if (doc == null)
        {
            logger.log(Level.SEVERE, "Документ не готов для загрузки сообщения");
            return;
        }
        
        linesCount++; // счёт строк
        
        Node body = doc.getElementsByTagName("BODY").item(0);
        Element div_element = doc.createElement("DIV");
        div_element.setAttribute("data-repainted", "false");
        String msgStyle = cm.getMsgColor() == null ? "" : String.format("background-color: #%1$s;", cm.getMsgColor());
        String msgClass = "new-line";
        
        if (cm.getMsgClass() != null)
            msgClass += " " + cm.getMsgClass();
        
        // если надо писать номера строк
        if (config.WriteMessageNumbers)
            msgClass += " " + "border";
        
        div_element.setAttribute("CLASS", msgClass);
        
        if (!msgStyle.isEmpty())
            div_element.setAttribute("STYLE", msgStyle);
        
        // добавляем нумерацию если надо писать номера строк
        if (config.WriteMessageNumbers)
        {
            Element kbd_element = doc.createElement("KBD");
            kbd_element.setTextContent(String.format("|%d|  ", linesCount));
            div_element.appendChild(kbd_element);
        }
        
        // добавляем основное содержание
        for (ContentElement ce : cm.get())
        {
            ArrayList <Element> elements = createElement(ce);
            if (elements != null)
                for (Element e : elements)
                {
                    div_element.appendChild(e);
                }
        }
        
        body.appendChild(div_element);
        
        // подрезаем по количеству сообщений
        if (config.ChatMaximumMessagesCount >= 10 && linesCount > config.ChatMaximumMessagesCount)
        {
            while (!body.getChildNodes().item(0).getNodeName().equals("DIV"))
            {
                body.removeChild(body.getChildNodes().item(0));
            }
            body.removeChild(body.getChildNodes().item(0));
            body.insertBefore(doc.createElement("HR"), body.getChildNodes().item(0));
        }
        
        // обновляем и прокручиваем
        repaintContentSmart();
        
        // обновляем данные у предка (вкладки)
        updateParent();
    }
    
    /**
     * Добавить к Логу новое сообщение
     */
    public void addMessage(LogMessage lm)
    {
        if (lm.get().isEmpty())
            return;
        
        if (!ready)
        {
            logger.log(Level.SEVERE, "WebView занят");
            return;
        }
        
        Document doc = content.getEngine().getDocument();
        if (doc == null)
        {
            logger.log(Level.SEVERE, "Документ не готов для загрузки сообщения");
            return;
        }
        
        Node body = doc.getElementsByTagName("BODY").item(0);
        
        Element div_element = doc.createElement("DIV");
        String msgClass = "new-line";
        
        if (lm.getMsgClass() != null)
            msgClass += " " + lm.getMsgClass();
        
        div_element.setAttribute("CLASS", msgClass);
        
        for (ContentElement ce : lm.get())
        {
            ArrayList <Element> elements = createElement(ce);
            if (elements != null)
                for (Element e : elements)
                {
                    div_element.appendChild(e);
                }
        }
        
        body.appendChild(div_element);
        linesCount++;
        
        executeScript("script_repaint_message");// здесь пока делаем это всегда
        if (autoScroll)
            executeScript("script_scroll");
    }
    
    /**
     * получить документ в виде текста
     */
    private String getDocumentText()
    {
        Document doc = content.getEngine().getDocument();
        
        if (doc == null)
        {
            logger.log(Level.SEVERE, "Документ не готов");
            return "";
        }
        
        String text_content = doc.getElementsByTagName("BODY").item(0).getTextContent();
        String title = content.getEngine().getTitle();
        
        return String.format("%1$s\n%2$s", title, text_content);
    }
    
    /**
     * Собрать элемент для отображения
     */
    private ArrayList <Element> createElement(ContentElement ce)
    {
        Document doc = content.getEngine().getDocument();
        
        if (doc == null)
        {
            logger.log(Level.SEVERE, "Документ не готов");
            return null;
        }
        
        ArrayList <Element> elements = new ArrayList <>();
        
        // текстовая ссылка
        if (ce.link != null && ce.text != null)
        {
            String key = "";
            String value = "";
            for (Map.Entry <String, String> entry : ce.link.entrySet())
            {
                key = entry.getKey();
                value = entry.getValue();
            }
            Element element = doc.createElement("A");
            element.setAttribute("HREF", "#");
            element.setAttribute("onclick",
                                 String.format("jsBridge.linkMouseClick('%1$s', '%2$s');return false;", key, value));
            element.setAttribute("onmouseover",
                                 String.format("jsBridge.linkMouseOver('%1$s', '%2$s');return false;", key, value));
            element.setAttribute("onmouseout", "jsBridge.linkMouseOut();return false;");
            if (ce.getElementClass() != null)
                element.setAttribute("CLASS", ce.getElementClass());
            element.setTextContent(ce.text);
            elements.add(element);
            return elements;
        }
        
        // просто текст
        else if (ce.text != null)
        {
            // распознавание УРЛов
            if (config.RecognizeURLInMessage)
                elements.addAll(ContentElementRecognizeURL(ce));
            else
            {
                Element element = doc.createElement("SPAN");
                if (ce.getElementClass() != null)
                    element.setAttribute("CLASS", ce.getElementClass());
                element.setTextContent(ce.text);
                elements.add(element);
            }
            return elements;
        }
        
        // не должны оказываться здесь
        logger.log(Level.SEVERE, "Ошибка создания элемента сообщения");
        return null;
    }
    
    /**
     * Разложить {@link ContentElement} на список с распознаванием URL-ов.
     * Применимо к элементам с простым текстовым содержанием
     */
    private ArrayList <Element> ContentElementRecognizeURL(ContentElement ce)
    {
        Document doc = content.getEngine().getDocument();
        ArrayList <Element> elements = new ArrayList <>();
        
        int pos = 0;
        TreeMap <Integer, String> urls = TextFunc.applyRegExp(ce.text, config.URLRegexp);
        
        if (urls == null)
        {
            Element element = doc.createElement("SPAN");
            if (ce.getElementClass() != null)
                element.setAttribute("CLASS", ce.getElementClass());
            element.setTextContent(ce.text);
            elements.add(element);
            return elements;
        }
        
        try
        {
            for (Map.Entry <Integer, String> entry : urls.entrySet())
            {
                String subtext = ce.text.substring(pos, entry.getKey());
                if (subtext.length() > 0)
                {
                    Element element = doc.createElement("SPAN");
                    if (ce.getElementClass() != null)
                        element.setAttribute("CLASS", ce.getElementClass());
                    element.setTextContent(subtext);
                    elements.add(element);
                }
                
                Element element = doc.createElement("A");
                element.setAttribute("HREF", "#");
                element.setAttribute("ONCLICK",
                                     String.format("jsBridge.linkMouseClick('url', '%1$s');", entry.getValue()));
                if (ce.getElementClass() != null)
                    element.setAttribute("CLASS", ce.getElementClass());
                element.setTextContent(entry.getValue());
                elements.add(element);
                pos += entry.getKey() + entry.getValue().length();
            }
            
            if (pos < ce.text.length())
            {
                Element element = doc.createElement("SPAN");
                if (ce.getElementClass() != null)
                    element.setAttribute("CLASS", ce.getElementClass());
                element.setTextContent(ce.text.substring(pos, ce.text.length()));
                elements.add(element);
            }
        }
        catch (Exception e)
        {
            config.RecognizeURLInMessage = false;
            logger.log(Level.SEVERE, "Ошибка при распознавании URLов", e);
            Platform.runLater(() -> scene.logContent
                    .addMessage(new LogMessage(new ContentElement("Ошибка при распознавании URL. Опция отключена."),
                                               LogMessage.MESSAGE_ERROR)));
            
            Element element = doc.createElement("SPAN");
            if (ce.getElementClass() != null)
                element.setAttribute("CLASS", ce.getElementClass());
            element.setTextContent(ce.text);
            elements.add(element);
        }
        
        return elements;
    }
    
    /**
     * добавить разделитель в документ
     */
    public void addSeparator(String text)
    {
        Document doc = content.getEngine().getDocument();
        
        if (doc == null)
        {
            logger.log(Level.SEVERE, "Документ не готов");
            return;
        }
        
        Element element_hr = doc.createElement("HR");
        Element element_br = doc.createElement("BR");
        element_br.setAttribute("CLEAR", "all");
        
        Node body = doc.getElementsByTagName("BODY").item(0);
        body.appendChild(element_br);
        
        if (text != null)
        {
            Element element_kbd = doc.createElement("KBD");
            element_kbd.setTextContent(String.format("[%s]\n", text));
            body.appendChild(element_kbd);
        }
        body.appendChild(element_hr);
        body.appendChild(element_br);
        if (autoScroll)
            executeScript("script_scroll");
        
        updateParent();
    }
    
    /**
     * установить автоскроллирование, поумолчанию = TRUE
     */
    private void setAutoScroll(boolean autoScroll)
    {
        this.autoScroll = autoScroll;
    }
    
    /**
     * отобразить/скрыть панель поиска
     */
    public void updateSearchPane()
    {
        // для веб-навигатора не используем
        if (modeWebNavigation)
            return;
        
        if (getChildren().contains(searchPane))
            getChildren().remove(searchPane);
        else //if (false)   // панель поиска временно отключена
        {
            textFieldSearch.getItems().setAll(config.SearchHistory);
            setBottom(searchPane);
            textFieldSearch.requestFocus();
        }
    }
    
    /**
     * отобразить панель поиска и начать поиск
     * @param text что ищем
     */
    public void searchText(String text)
    {
        // для веб-навигатора не используем
        if (modeWebNavigation)
            return;
        
        if (!getChildren().contains(searchPane))
        {
            textFieldSearch.getItems().setAll(config.SearchHistory);
            setBottom(searchPane);
        }
        
        textFieldSearch.getEditor().setText(text);
        content.requestFocus();
        searchText(text, -1, "script_search_down");
    }
    
    /**
     * Поиск в документе
     * @param text       что ищем
     * @param index      стартовый элемент
     * @param scriptName путь до скрипта в ресурсах приложения
     */
    private void searchText(String text, int index, String scriptName)
    {
        if (text == null || text.isEmpty())
            return;
        
        config.SearchHistory.remove(text);
        config.SearchHistory.add(0, text);
        if (config.SearchHistory.size() > config.SearchHistorySize)
            config.SearchHistory = new ArrayList <>(config.SearchHistory.subList(0, config.SearchHistorySize));
        textFieldSearch.getItems().setAll(config.SearchHistory);
        
        String script = scripts.get(scriptName);
        
        searchIndex = (int) executeTextScript(String.format(script, text, index));
    }
    
    /**
     * выполнить javascript
     * @param script ключ скрипта
     */
    private Object executeScript(String script)
    {
        String scriptText = scripts.get(script);
        try
        {
            logger.log(Level.INFO, String.format("Выполнение скрипта \"%s\"", script));
            Object o = content.getEngine().executeScript(scriptText);
            logger.log(Level.INFO, String.format("Выполнение скрипта \"%s\": готово", script));
            return o;
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка выполнения скрипта", e);
        }
        return null;
    }
    
    /**
     * выполнить javascript
     * @param scriptText текст скрипта
     */
    private Object executeTextScript(String scriptText)
    {
        try
        {
            logger.log(Level.INFO, String.format("Выполнение скрипта:\n{%s}", scriptText));
            Object o = content.getEngine().executeScript(scriptText);
            logger.log(Level.INFO, "Выполнение скрипта: готово");
            return o;
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка выполнения скрипта", e);
        }
        return null;
    }
    
    /**
     * Передача данных родителю в режиме чата
     */
    private void updateParent()
    {
        // для веб-навигатора не используем
        if (modeWebNavigation)
            return;
        
        if (parent == null)
            return;
        
        if (!(parent instanceof Tab))
            return;
        
        String tabCaption = TextFunc.SetFixedSize(title, config.ContentTabCaptionLength);
        
        if (config.WriteContentSizeInTabs)
        {
            String s = (String) executeScript("script_get_html");
            long countBytes = s != null ? s.length() : 0;
            
            ((Tab) parent).setText(String.format("%1$s [%2$d | %3$s]",
                                                 tabCaption,
                                                 linesCount,
                                                 TextFunc.humanReadableByteCount(countBytes, true)));
        }
        else
            ((Tab) parent).setText(String.format("%1$s [%2$d]", tabCaption, linesCount));
    }
    
    /**
     * выполнить JS скрипт, загрузив его из файла
     */
    private void ExecuteScriptFromFile()
    {
        String file = FileFunc.showOpenFileDialog(scene.getWindow(),
                                                  ApplicationControl.MyWorkingDir,
                                                  new FileChooser.ExtensionFilter("JS scripts (*.js)", "*.js"));
        
        if (file == null || file.isEmpty())
            return;
        
        String script = FileFunc.fileToString(file, config.Encoding);
        logger.log(Level.INFO, "Результат выполнения скрипта: " + String.valueOf(executeTextScript(script)));
    }
    
    /**
     * сохранение текста в файл  <br>
     * @param file файл, в который сохраняем
     * @return код ошибки: -2 - моя ошибка, -1 - FileFunc.saveTextToFile
     * @see FileFunc
     */
    public final long SaveAllToTXTFile(File file)
    {
        String text = getDocumentText();
        
        if (text.length() <= 0)
        {
            CommonDialogs.ShowMessage(Alert.AlertType.ERROR, "Ошибка", null, "Нечего сохранять!", null);
            return -2;
        }
        
        if (config.ToSaveTextsForWindowsOS)
            text = text.replace("\n", "\r\n");
        
        if (FileFunc.saveTextToFile(file.getAbsolutePath(), text, config.Encoding, false))
            return file.length();
        else
            return -1;
    }
    
    /**
     * сохранение HTML в файл  <br>
     * @param file файл, в который сохраняем
     * @return код ошибки: -2 - моя ошибка, -1 - FileFunc.saveTextToFile
     * @see FileFunc
     */
    public final long SaveAllToHTMLFile(File file)
    {
        String text = (String) executeScript("script_get_html");
        
        if (text == null || text.length() <= 0)
        {
            CommonDialogs.ShowMessage(Alert.AlertType.ERROR, "Ошибка", null, "Нечего сохранять!", null);
            return -2;
        }
        
        text = "<!DOCTYPE html>\n" + text;
        
        if (FileFunc.saveTextToFile(file.getAbsolutePath(), text, config.Encoding, false))
            return file.length();
        else
            return -1;
    }
    
    /**
     * Обновить содержание полностью
     */
    public void repaintAllContent()
    {
        String count = String.valueOf(executeScript("script_repaint_all"));
        logger.log(Level.INFO, String.format("Перерисовано %s элементов документа", count));
        executeScript("script_scroll");
    }
    
    /**
     * обновить содержание при активации (переключении вкладок) с учётом всех параметров
     */
    public void repaintActiveContent()
    {
        if (modeWebNavigation)
            return;
        
        // выполнение скрипта по обновлению сообщений
        if (config.RepaintNodesAtNewMessage)
        {
            String count = String.valueOf(executeScript("script_repaint_by_flag"));
            logger.log(Level.INFO, String.format("Перерисовано %s элементов документа", count));
        }
        // прокрутка чата до сообщения
        if (autoScroll)
            executeScript("script_scroll");
    }
    
    /**
     * Избирательное обновление (только на активной вкладке) с учётом всех параметров
     */
    private void repaintContentSmart()
    {
        if (modeWebNavigation)
            return;
        
        if (parent == null)
            return;
        
        if (!(parent instanceof Tab))
            return;
        
        if (((Tab) parent).isSelected())
        {
            // выполнение скрипта по обновлению сообщений
            if (config.RepaintNodesAtNewMessage)
                executeScript("script_repaint_message");
            
            // прокрутка чата до сообщения
            if (autoScroll)
                executeScript("script_scroll");
        }
    }
    
    /**
     * Перезагрузить страницу
     */
    private void reloadContent()
    {
        if (modeWebNavigation)
            content.getEngine().reload();
        else
        {
            Document doc = content.getEngine().getDocument();
            
            if (doc == null)
            {
                logger.log(Level.SEVERE, "Документ не готов");
                return;
            }
            
            Node node = doc.getElementsByTagName("HEAD").item(0);
            
            for (int i = 0; i < node.getChildNodes().getLength(); i++)
            {
                if (node.getChildNodes().item(i).getNodeName().toLowerCase().equals("style"))
                {
                    node.getChildNodes().item(i).setTextContent(createHeadStyle());
                    break;
                }
            }
            
            node = doc.getElementsByTagName("BODY").item(0);
            node.getAttributes().getNamedItem("style").setNodeValue(createBodyStyle());
            
            String text = (String) executeScript("script_get_html");
            content.getEngine().loadContent(text);
        }
    }
    
    /**
     * отобразить панель веб-навигации
     */
    private void showWebNavigationPane()
    {
        if (getChildren().contains(progressBar))
            getChildren().remove(progressBar);
        
        if (getChildren().contains(webnavPane))
            getChildren().remove(webnavPane);
        else
        {
            textWebNav.getItems().setAll(config.WebNavigationHistory);
            setTop(webnavPane);
            textWebNav.requestFocus();
        }
    }
    
    /**
     * открыть веб-адрес
     */
    private void openWebAddress(String address)
    {
        if (address == null || address.isEmpty())
            return;
        
        getChildren().remove(webnavPane);
        config.WebNavigationHistory.remove(address);
        config.WebNavigationHistory.add(0, address);
        
        if (config.WebNavigationHistory.size() > config.SearchHistorySize)
            config.WebNavigationHistory =
                    new ArrayList <>(config.WebNavigationHistory.subList(0, config.SearchHistorySize));
        
        textWebNav.getItems().setAll(config.WebNavigationHistory);
        
        content.getEngine().load(address);
        getChildren().remove(webnavPane);
    }
    
    /**
     * установить масштаб шрифта
     */
    public void setFontScale(double value)
    {
        content.setFontScale(content.getFontScale() * value);
    }
    
    /**
     * получить WebView компонент
     */
    public WebView getContent()
    {
        return content;
    }
    
    /**
     * сгенерировать стиль Head документа
     */
    private String createHeadStyle()
    {
        return String.format(HeadStyleTemplate,
                             (int) config.CommonFontSize,
                             config.WebViewFontFamily,
                             100 * config.CommonFontWeight,
                             config.EventOtherWebColor,
                             config.CommonDataTimeWebColor,
                             config.ChatMessageSourceWebColor,
                             config.ChatMessageChannelWebColor,
                             config.ChatMessageAuthorWebColor,
                             config.ChatMessageMessageWebColor,
                             config.EventErrorWebColor,
                             config.EventInfoWebColor,
                             config.EventSeccessWebColor,
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("bubble16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("info16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("next16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("prev16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("service16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("chat16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("red_bookmark16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("OK16"), "png"),
                             ImgFunc.LoadBase64FromImage(ResFunc.getImage("error16"), "png"));
    }
    
    /**
     * сгенерировать стиль Body документа
     */
    private String createBodyStyle()
    {
        String backgroundColor = TextFunc.ColorToRGBCode(Color.web(config.ThemeWebColor).brighter());
        return String.format(BodyStyleTemplate, backgroundColor);
    }
    
    /**
     * регистрирую внутренний обработчик js-скриптов {@link JSBridge}
     */
    private void registerJSBridge()
    {
        JSObject window = (JSObject) content.getEngine().executeScript("window");
        window.setMember("jsBridge", jsBridge);
        logger.log(Level.INFO, "jsBridge назначен: " + window.getMember("jsBridge").getClass().getName());
    }
}