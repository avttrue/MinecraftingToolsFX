package mcru.MinecraftingTools.Dialogs;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mcru.MinecraftingTools.Functions.FileFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.ContentElement;
import mcru.MinecraftingTools.Helpers.JSBridge;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Helpers.MyLink;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.MinecraftingAPI.MinecraftingDBListBuilder;
import mcru.MinecraftingTools.MojangAPI.MojangDBListBuilder;
import mcru.MinecraftingTools.MyApplication;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Functions.ResFunc.getScript;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Диалог отображения информации в HTML формате
 */
public class WebViewInfoDialog
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    private WebView content;
    private ProgressBar progressBar;
    private Label labelProgress;
    private Task <String> task;
    private BorderPane borderPane;
    private HBox boxProgress;
    private Dialog dialog;
    private HBox searchPane = new HBox();
    private ComboBox <String> textFieldSearch = new ComboBox <>();
    private int searchIndex = -1;
    private ContextMenu contextMenu = new ContextMenu();
    private JSBridge jsBridge = new JSBridge();
    private Worker <Void> worker;
    
    /**
     * Диалог отображения информации в HTML формате
     * @param owner Окно-владелец
     * @param title Заголовок
     * @param task  ЗаданиеЮ формирующие содержание
     */
    public WebViewInfoDialog(Window owner, String title, Task <String> task)
    {
        this.task = task;
        dialog = new javafx.scene.control.Dialog <>();
        dialog.initOwner(owner);
        dialog.setTitle(title);
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefSize(config.InfoWindowWidth, config.InfoWindowHeight);
        dialog.setGraphic(null);
        
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        
        ((javafx.scene.control.Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        ((javafx.scene.control.Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setOnAction(lambda -> task.cancel(true));
        
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        borderPane = new BorderPane();
        borderPane.setBorder(new Border(new MyBorder(5, 1)));
        
        content = new WebView();
        // отмена DnD
        content.setOnDragEntered(Event::consume);
        content.setOnDragOver(Event::consume);
        content.setOnDragExited(Event::consume);
        content.setOnDragDropped(Event::consume);
        content.setOnDragDone(Event::consume);
        
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
        content.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED)
            {
                registerJSBridge();
                jsBridge.toListenMouseMove = false;
            }
        });
        
        progressBar = new ProgressBar();
        labelProgress = new Label();
        boxProgress = new HBox(progressBar, labelProgress);
        boxProgress.setAlignment(Pos.CENTER);
        boxProgress.setSpacing(10);
        
        // панель поиска
        searchPane.setBorder(new Border(new MyBorder(5, 1)));
        
        Button closeSearch = new Button("", new ImageView(ResFunc.getImage("cancel16")));
        closeSearch.setTooltip(new Tooltip("Скрыть панель поиска"));
        closeSearch.setOnAction(lambda -> {
            borderPane.getChildren().remove(searchPane);
            searchIndex = -1;
        });
        
        textFieldSearch.setPrefWidth(Integer.MAX_VALUE);
        textFieldSearch.setEditable(true);
        
        Button upSearch = new Button("", new ImageView(ResFunc.getImage("up16")));
        upSearch.setTooltip(new Tooltip("Искать выше"));
        upSearch.setOnAction(lambda -> {
            this.content.requestFocus();
            searchText(textFieldSearch.getEditor().getText(), searchIndex, "script_search_up");
        });
        
        Button downSearch = new Button("", new ImageView(ResFunc.getImage("down16")));
        downSearch.setTooltip(new Tooltip("Искать ниже"));
        downSearch.setOnAction(lambda -> {
            this.content.requestFocus();
            searchText(textFieldSearch.getEditor().getText(), searchIndex, "script_search_down");
        });
        
        worker = content.getEngine().getLoadWorker();
        ChangeListener <? super Worker.State> changeListener =
                (ChangeListener <Worker.State>) (observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED || newValue == Worker.State.FAILED)
                    {
                        borderPane.getChildren().remove(boxProgress);
                    }
                };
        worker.stateProperty().addListener(changeListener);
        
        searchPane.getChildren().addAll(closeSearch, textFieldSearch, upSearch, downSearch);
        borderPane.setCenter(content);
        dialog.getDialogPane().setContent(borderPane);
        dialog.show();
    }
    
    /**
     * выполнить задание на формирование содержания
     */
    public void startTask()
    {
        borderPane.setTop(boxProgress);
        
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());
        labelProgress.textProperty().unbind();
        labelProgress.textProperty().bind(task.messageProperty());
        
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            
            labelProgress.textProperty().unbind();
            progressBar.progressProperty().unbind();
            
            int count = 0;
            if (task instanceof MojangDBListBuilder)
                count = ((MojangDBListBuilder) task).getCountPlayers();
            else if (task instanceof MinecraftingDBListBuilder)
                count = ((MinecraftingDBListBuilder) task).getCountPlayers();
            
            dialog.setTitle(String.format("%s (%d записей)", dialog.getTitle(), count));
            
            Platform.runLater(() -> loadContent(task.getValue()));
        });
        
        new Thread(task).start();
    }
    
    /**
     * загрузка содержания
     */
    private void loadContent(String text)
    {
        if (!borderPane.getChildren().contains(boxProgress))
            borderPane.setTop(boxProgress);
        
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(worker.progressProperty());
        
        labelProgress.textProperty().unbind();
        labelProgress.setText(" Загрузка содержания ...");
        
        content.getEngine().loadContent(text);
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
        
        Node node_head = doc.getElementsByTagName("BODY").item(0);
        
        return content.getEngine().getTitle() + "\n" + node_head.getTextContent();
    }
    
    /**
     * сохранение текста в файл  <br>
     * @param file файл, в который сохраняем
     * @return код ошибки: -2 - моя ошибка, -1 - FileFunc.saveTextToFile
     * @see FileFunc
     */
    private long SaveAllToTXTFile(File file)
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
    private long SaveAllToHTMLFile(File file)
    {
        String text = (String) executeTextScript(getScript("script_get_html"));
        
        if (text != null && text.length() <= 0)
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
     * выполнить javascript
     * @param scriptText текст скрипта
     */
    private Object executeTextScript(String scriptText)
    {
        try
        {
            logger.log(Level.INFO, String.format("Выполнение скрипта:\n{%s}", scriptText));
            return content.getEngine().executeScript(scriptText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка выполнения скрипта", e);
        }
        return null;
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
        
        String script = getScript(scriptName);
        
        if (script == null || script.isEmpty())
            return;
        
        searchIndex = (int) executeTextScript(String.format(script, text, index));
    }
    
    /**
     * Создание контекстного меню
     */
    private ContextMenu CreateContextMenu()
    {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem copyMenuItem = new MenuItem("Копировать", new ImageView(ResFunc.getImage("copy16")));
        copyMenuItem.setOnAction(lambda -> {
            String selection = (String) executeTextScript(getScript("script_get_selection"));
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
        
        MenuItem searchMenuItem = new MenuItem("Поиск", new ImageView(ResFunc.getImage("search16")));
        searchMenuItem.setOnAction(lambda -> updateSearchPane());
        
        MenuItem saveItem = new MenuItem("Сохранить...", new ImageView(ResFunc.getImage("save16")));
        saveItem.setOnAction(lambda -> saveCurrentChatTab());
        
        MenuItem reloadItem = new MenuItem("Перезагрузить документ", new ImageView(ResFunc.getImage("update16")));
        reloadItem.setOnAction(lambda -> reloadContent());
        
        contextMenu.getItems().addAll(copyMenuItem, copyallMenuItem, searchMenuItem, saveItem, reloadItem);
        return contextMenu;
    }
    
    /**
     * отобразить/скрыть панель поиска
     */
    private void updateSearchPane()
    {
        if (borderPane.getChildren().contains(searchPane))
            borderPane.getChildren().remove(searchPane);
        else
        {
            textFieldSearch.getItems().setAll(config.SearchHistory);
            borderPane.setBottom(searchPane);
            textFieldSearch.requestFocus();
        }
    }
    
    /**
     * сохранить в файл содержание открытой вкладки чата
     */
    private void saveCurrentChatTab()
    {
        String file = FileFunc.showSaveFileDialog(scene.getWindow(),
                                                  "Сохранить документ",
                                                  new FileChooser.ExtensionFilter("документы HTML (*.html)", "*.html"),
                                                  new FileChooser.ExtensionFilter("текстовые документы (*.txt)",
                                                                                  "*.txt"));
        if (file.isEmpty())
            return;
        
        long result = 0;
        if (file.toLowerCase().endsWith(".txt"))
            result = SaveAllToTXTFile(new File(file));
        else if (file.toLowerCase().endsWith(".html"))
            result = SaveAllToHTMLFile(new File(file));
        
        if (result > -1)
        {
            ArrayList <ContentElement> message = new ArrayList <>();
            message.add(new ContentElement("Данные сохранены в файл: "));
            message.add(new ContentElement(file, new MyLink("file", TextFunc.SetPathToHTML(file)).get())
                                .addElementClass("info-message"));
            message.add(new ContentElement(String.format(", размер %1$s ",
                                                         TextFunc.humanReadableByteCount(result, true))));
            Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_INFO)));
        }
        else if (result == -1)
            Platform.runLater(() -> scene.logContent
                    .addMessage(new LogMessage(new ContentElement("Сохранить данные в файл не удалось"),
                                               LogMessage.MESSAGE_ERROR)));
    }
    
    /**
     * обновить содержание
     */
    private void reloadContent()
    {
        loadContent(task.getValue());
    }
    
    /**
     * регистрирую внутренний обработчик js-скриптов {@link JSBridge}
     */
    private void registerJSBridge()
    {
        JSObject window = (JSObject) content.getEngine().executeScript("window");
        window.setMember("jsBridge", jsBridge);
        logger.log(Level.INFO, "jsBridge назначен: " + window.getMember("jsBridge").getClass().getCanonicalName());
    }
}
