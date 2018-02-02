package mcru.MinecraftingTools.Dialogs;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mcru.MinecraftingTools.Functions.FileFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.HighlightElement;
import mcru.MinecraftingTools.Interface.*;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.Sound.SoundPlayer;

import java.io.File;
import java.util.ArrayList;

import static mcru.MinecraftingTools.ApplicationControl.MyWorkingDir;
import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Диалог настроек приложения
 */
public class ConfigHighlightDialog
{
    private ListView <HighlightElement> lvMessageHL = new ListView <>();
    private ListView <HighlightElement> lvChannelHL = new ListView <>();
    private ListView <HighlightElement> lvSourceHL = new ListView <>();
    private BorderPane paneMessageHL = new BorderPane();
    private BorderPane paneChannelHL = new BorderPane();
    private BorderPane paneSourceHL = new BorderPane();
    private CheckBox cbMessageHLIsOn;
    private CheckBox cbChannelHLIsOn;
    private CheckBox cbSourceHLIsOn;
    private SelectFilePane sfpMessageHLSound;
    private SelectFilePane sfpChannelHLSound;
    private SelectFilePane sfpSourceHLSound;
    private ColorPane cpMessageHLColor;
    private ColorPane cpSourceHLColor;
    private ColorPane cpChannelHLColor;
    private TextFieldPane tfpMessageHLCaption;
    private TextFieldPane tfpMessageHLRegexp;
    private TextFieldPane tfpChannelHLCaption;
    private TextFieldPane tfpChannelHLRegexp;
    private TextFieldPane tfpSourceHLCaption;
    private TextFieldPane tfpSourceHLRegexp;
    
    public ConfigHighlightDialog(Window parent)
    {
        Dialog <AuthentificationData> dialog = new Dialog <>();
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initOwner(parent);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle("Настройка подсветок");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(config.ConfigWindowWidth, config.ConfigWindowHeight);
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        ((Button) dialog.getDialogPane().lookupButton(okButtonType)).setText("");
        ((Button) dialog.getDialogPane().lookupButton(okButtonType)).setOnAction(lambda -> clickOK());
        
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setGraphic(new ImageView(ResFunc.getImage("cancel24")));
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("");
        
        lvMessageHL.getItems().addAll(config.MessageHighlightsList);
        lvMessageHL.getSelectionModel().
                selectedItemProperty().
                           addListener((ObservableValue <? extends HighlightElement> ov, HighlightElement old_val, HighlightElement new_val) -> selectMessageHL(
                                   new_val));
        
        tfpMessageHLCaption = new TextFieldPane("Название", "");
        tfpMessageHLRegexp = new TextFieldPane("Рег. выражение:", "");
        sfpMessageHLSound = new SelectFilePane("Включить звук",
                                               false,
                                               "",
                                               lambda -> openHLSound(sfpMessageHLSound),
                                               lambda -> returnHLSound(lvMessageHL, sfpMessageHLSound),
                                               lambda -> checkHLSound(sfpMessageHLSound));
        
        cpMessageHLColor = new ColorPane("Цвет подсветки", null, Color.WHITE);
        cpMessageHLColor.getStyleClass().add("split-button");
        
        cbMessageHLIsOn = new CheckBox(" Включить ");
        cbMessageHLIsOn.getStyleClass().add("PANEL-SPECIAL");
        
        VBox vbMessageHL = new VBox(tfpMessageHLCaption,
                                    tfpMessageHLRegexp,
                                    sfpMessageHLSound,
                                    cpMessageHLColor,
                                    new Separator(),
                                    cbMessageHLIsOn);
        vbMessageHL.setSpacing(1);
        vbMessageHL.getStyleClass().add("-fx-font-family: monospace");
        vbMessageHL.setPadding(new Insets(5, 5, 5, 5));
        
        Button buttonMessageHLDelete = new Button("", new ImageView(ResFunc.getImage("minus16")));
        buttonMessageHLDelete.setTooltip(new Tooltip("Удалить запись"));
        buttonMessageHLDelete.setOnAction(lambda -> removeRecordHL(lvMessageHL));
        
        Button buttonMessageHLAdd = new Button("", new ImageView(ResFunc.getImage("plus16")));
        buttonMessageHLAdd.setTooltip(new Tooltip("Добавить запись"));
        buttonMessageHLAdd.setOnAction(lambda -> addRecordHL(lvMessageHL));
        
        Button buttonMessageHLAccept = new Button("", new ImageView(ResFunc.getImage("OK16")));
        buttonMessageHLAccept.setTooltip(new Tooltip("Принять изменения"));
        buttonMessageHLAccept.setOnAction(lambda -> acceptRecordMessageHL());
        
        Button buttonMessageHLLoad = new Button("", new ImageView(ResFunc.getImage("text16")));
        buttonMessageHLLoad.setTooltip(new Tooltip("Загрузить список из файла"));
        buttonMessageHLLoad.setOnAction(lambda -> loadRegexpHL(tfpMessageHLRegexp));
        
        paneMessageHL.setCenter(vbMessageHL);
        paneMessageHL.setBottom(new ToolBar(new HorizontalSpacer(),
                                            buttonMessageHLDelete,
                                            buttonMessageHLAdd,
                                            buttonMessageHLLoad,
                                            buttonMessageHLAccept,
                                            new HorizontalSpacer()));
        paneMessageHL.getCenter().setDisable(true);
        
        ScrollPane spMessageHL = new ScrollPane(paneMessageHL);
        spMessageHL.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spMessageHL.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        spMessageHL.setFitToWidth(true);
        
        SplitPane splpMessageHL = new SplitPane(new GroupBox("Список подсветок", lvMessageHL, true),
                                                new GroupBox("Редактирование", spMessageHL, true));
        splpMessageHL.setOrientation(Orientation.VERTICAL);
        
        //подсветка Каналов
        lvChannelHL.getItems().addAll(config.ChannelHighlightsList);
        lvChannelHL.getSelectionModel().
                selectedItemProperty().
                           addListener((ObservableValue <? extends HighlightElement> ov, HighlightElement old_val, HighlightElement new_val) -> selectChannelHL(
                                   new_val));
        
        tfpChannelHLCaption = new TextFieldPane("Название", "");
        tfpChannelHLRegexp = new TextFieldPane("Рег. выражение:", "");
        sfpChannelHLSound = new SelectFilePane("Включить звук",
                                               false,
                                               "",
                                               lambda -> openHLSound(sfpChannelHLSound),
                                               lambda -> returnHLSound(lvChannelHL, sfpChannelHLSound),
                                               lambda -> checkHLSound(sfpChannelHLSound));
        
        cpChannelHLColor = new ColorPane("Цвет подсветки", null, Color.WHITE);
        cpChannelHLColor.getStyleClass().add("split-button");
        
        cbChannelHLIsOn = new CheckBox(" Включить ");
        cbChannelHLIsOn.getStyleClass().add("PANEL-SPECIAL");
        
        VBox vbChannelHL = new VBox(tfpChannelHLCaption,
                                    tfpChannelHLRegexp,
                                    sfpChannelHLSound,
                                    cpChannelHLColor,
                                    new Separator(),
                                    cbChannelHLIsOn);
        vbChannelHL.setSpacing(1);
        vbChannelHL.getStyleClass().add("-fx-font-family: monospace");
        vbChannelHL.setPadding(new Insets(5, 5, 5, 5));
        
        Button buttonChannelHLDelete = new Button("", new ImageView(ResFunc.getImage("minus16")));
        buttonChannelHLDelete.setTooltip(new Tooltip("Удалить запись"));
        buttonChannelHLDelete.setOnAction(lambda -> removeRecordHL(lvChannelHL));
        
        Button buttonChannelHLAdd = new Button("", new ImageView(ResFunc.getImage("plus16")));
        buttonChannelHLAdd.setTooltip(new Tooltip("Добавить запись"));
        buttonChannelHLAdd.setOnAction(lambda -> addRecordHL(lvChannelHL));
        
        Button buttonChannelHLAccept = new Button("", new ImageView(ResFunc.getImage("OK16")));
        buttonChannelHLAccept.setTooltip(new Tooltip("Принять изменения"));
        buttonChannelHLAccept.setOnAction(lambda -> acceptRecordChannelHL());
        
        Button buttonChannelHLLoad = new Button("", new ImageView(ResFunc.getImage("text16")));
        buttonChannelHLLoad.setTooltip(new Tooltip("Загрузить список из файла"));
        buttonChannelHLLoad.setOnAction(lambda -> loadRegexpHL(tfpChannelHLRegexp));
        
        paneChannelHL.setCenter(vbChannelHL);
        paneChannelHL.setBottom(new ToolBar(new HorizontalSpacer(),
                                            buttonChannelHLDelete,
                                            buttonChannelHLAdd,
                                            buttonChannelHLLoad,
                                            buttonChannelHLAccept,
                                            new HorizontalSpacer()));
        paneChannelHL.getCenter().setDisable(true);
        
        ScrollPane spChannelHL = new ScrollPane(paneChannelHL);
        spChannelHL.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spChannelHL.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        spChannelHL.setFitToWidth(true);
        
        SplitPane splpChannelHL = new SplitPane(new GroupBox("Список подсветок", lvChannelHL, true),
                                                new GroupBox("Редактирование", spChannelHL, true));
        splpChannelHL.setOrientation(Orientation.VERTICAL);
        
        //подсветка источников
        lvSourceHL.getItems().addAll(config.SourceHighlightsList);
        lvSourceHL.getSelectionModel().
                selectedItemProperty().
                          addListener((ObservableValue <? extends HighlightElement> ov, HighlightElement old_val, HighlightElement new_val) -> selectSourceHL(
                                  new_val));
        
        tfpSourceHLCaption = new TextFieldPane("Название", "");
        tfpSourceHLRegexp = new TextFieldPane("Рег. выражение:", "");
        sfpSourceHLSound = new SelectFilePane("Включить звук",
                                              false,
                                              "",
                                              lambda -> openHLSound(sfpSourceHLSound),
                                              lambda -> returnHLSound(lvSourceHL, sfpSourceHLSound),
                                              lambda -> checkHLSound(sfpSourceHLSound));
        
        cpSourceHLColor = new ColorPane("Цвет подсветки", null, Color.WHITE);
        cpSourceHLColor.getStyleClass().add("split-button");
        
        cbSourceHLIsOn = new CheckBox(" Включить ");
        cbSourceHLIsOn.getStyleClass().add("PANEL-SPECIAL");
        
        
        VBox vbSourceHL = new VBox(tfpSourceHLCaption,
                                   tfpSourceHLRegexp,
                                   sfpSourceHLSound,
                                   cpSourceHLColor,
                                   new Separator(),
                                   cbSourceHLIsOn);
        vbSourceHL.setSpacing(1);
        vbSourceHL.getStyleClass().add("-fx-font-family: monospace");
        vbSourceHL.setPadding(new Insets(5, 5, 5, 5));
        
        Button buttonSourceHLDelete = new Button("", new ImageView(ResFunc.getImage("minus16")));
        buttonSourceHLDelete.setTooltip(new Tooltip("Удалить запись"));
        buttonSourceHLDelete.setOnAction(lambda -> removeRecordHL(lvSourceHL));
        
        Button buttonSourceHLAdd = new Button("", new ImageView(ResFunc.getImage("plus16")));
        buttonSourceHLAdd.setTooltip(new Tooltip("Добавить запись"));
        buttonSourceHLAdd.setOnAction(lambda -> addRecordHL(lvSourceHL));
        
        Button buttonSourceHLAccept = new Button("", new ImageView(ResFunc.getImage("OK16")));
        buttonSourceHLAccept.setTooltip(new Tooltip("Принять изменения"));
        buttonSourceHLAccept.setOnAction(lambda -> acceptRecordSourceHL());
        
        Button buttonSourceHLLoad = new Button("", new ImageView(ResFunc.getImage("text16")));
        buttonSourceHLLoad.setTooltip(new Tooltip("Загрузить список из файла"));
        buttonSourceHLLoad.setOnAction(lambda -> loadRegexpHL(tfpSourceHLRegexp));
        
        paneSourceHL.setCenter(vbSourceHL);
        paneSourceHL.setBottom(new ToolBar(new HorizontalSpacer(),
                                           buttonSourceHLDelete,
                                           buttonSourceHLAdd,
                                           buttonSourceHLLoad,
                                           buttonSourceHLAccept,
                                           new HorizontalSpacer()));
        paneSourceHL.getCenter().setDisable(true);
        
        ScrollPane spSourceHL = new ScrollPane(paneSourceHL);
        spSourceHL.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spSourceHL.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        spSourceHL.setFitToWidth(true);
        
        SplitPane splpSourceHL = new SplitPane(new GroupBox("Список подсветок", lvSourceHL, true),
                                               new GroupBox("Редактирование", spSourceHL, true));
        splpSourceHL.setOrientation(Orientation.VERTICAL);
        
        
        //////////////////////////////////////////////
        Tab tabHLChannelHighlights = new Tab("По каналу", splpChannelHL);
        tabHLChannelHighlights.setClosable(false);
        
        Tab tabHLMessageHighlights = new Tab("По сообщению", splpMessageHL);
        tabHLMessageHighlights.setClosable(false);
        
        Tab tabHLSourceHighlights = new Tab("По источнику", splpSourceHL);
        tabHLSourceHighlights.setClosable(false);
        
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tabHLMessageHighlights, tabHLChannelHighlights, tabHLSourceHighlights);
        
        dialog.getDialogPane().setContent(tabPane);
        dialog.showAndWait();
        
    }
    
    private void clickOK()
    {
        config.MessageHighlightsList = new ArrayList <>(lvMessageHL.getItems());
        config.ChannelHighlightsList = new ArrayList <>(lvChannelHL.getItems());
        config.SourceHighlightsList = new ArrayList <>(lvSourceHL.getItems());
    }
    
    /**
     * выбор элемента списка подсветок сообщений
     */
    private void selectMessageHL(HighlightElement hle)
    {
        paneMessageHL.getCenter().setDisable(true);
        
        if (hle == null)
            return;
        
        tfpMessageHLCaption.setValue(hle.caption);
        cbMessageHLIsOn.setSelected(hle.isOn);
        tfpMessageHLRegexp.setValue(hle.textRegexp);
        cpMessageHLColor.setValue(Color.web(hle.colorBackground));
        sfpMessageHLSound.setValue(hle.soundPath);
        sfpMessageHLSound.setSelected(hle.isPlaySound);
        
        paneMessageHL.getCenter().setDisable(false);
    }
    
    /**
     * выбор элемента списка подсветок каналов
     */
    private void selectChannelHL(HighlightElement hle)
    {
        paneChannelHL.getCenter().setDisable(true);
        
        if (hle == null)
            return;
        
        tfpChannelHLCaption.setValue(hle.caption);
        cbChannelHLIsOn.setSelected(hle.isOn);
        tfpChannelHLRegexp.setValue(hle.textRegexp);
        cpChannelHLColor.setValue(Color.web(hle.colorBackground));
        sfpChannelHLSound.setValue(hle.soundPath);
        sfpChannelHLSound.setSelected(hle.isPlaySound);
        
        paneChannelHL.getCenter().setDisable(false);
    }
    
    /**
     * выбор элемента списка подсветок источников
     */
    private void selectSourceHL(HighlightElement hle)
    {
        paneSourceHL.getCenter().setDisable(true);
        
        if (hle == null)
            return;
        
        tfpSourceHLCaption.setValue(hle.caption);
        cbSourceHLIsOn.setSelected(hle.isOn);
        tfpSourceHLRegexp.setValue(hle.textRegexp);
        cpSourceHLColor.setValue(Color.web(hle.colorBackground));
        sfpSourceHLSound.setValue(hle.soundPath);
        sfpSourceHLSound.setSelected(hle.isPlaySound);
        
        paneSourceHL.getCenter().setDisable(false);
    }
    
    /**
     * Принять изменения в записи в списоке подсветок сообщений
     */
    private void acceptRecordMessageHL()
    {
        HighlightElement hle = lvMessageHL.getSelectionModel().getSelectedItem();
        if (hle == null)
            return;
        
        hle.caption = tfpMessageHLCaption.getValue();
        hle.isOn = cbMessageHLIsOn.isSelected();
        hle.textRegexp = tfpMessageHLRegexp.getValue();
        hle.colorBackground = TextFunc.ColorToRGBCode(cpMessageHLColor.getValue());
        hle.soundPath = sfpMessageHLSound.getValue();
        hle.isPlaySound = sfpMessageHLSound.getSelected();
    }
    
    /**
     * Принять изменения в записи в списоке подсветок каналов
     */
    private void acceptRecordChannelHL()
    {
        HighlightElement hle = lvChannelHL.getSelectionModel().getSelectedItem();
        if (hle == null)
            return;
        
        hle.caption = tfpChannelHLCaption.getValue();
        hle.isOn = cbChannelHLIsOn.isSelected();
        hle.textRegexp = tfpChannelHLRegexp.getValue();
        hle.colorBackground = TextFunc.ColorToRGBCode(cpChannelHLColor.getValue());
        hle.soundPath = sfpChannelHLSound.getValue();
        hle.isPlaySound = sfpChannelHLSound.getSelected();
    }
    
    /**
     * Принять изменения в записи в списоке подсветок источников
     */
    private void acceptRecordSourceHL()
    {
        HighlightElement hle = lvSourceHL.getSelectionModel().getSelectedItem();
        if (hle == null)
            return;
        
        hle.caption = tfpSourceHLCaption.getValue();
        hle.isOn = cbSourceHLIsOn.isSelected();
        hle.textRegexp = tfpSourceHLRegexp.getValue();
        hle.colorBackground = TextFunc.ColorToRGBCode(cpSourceHLColor.getValue());
        hle.soundPath = sfpSourceHLSound.getValue();
        hle.isPlaySound = sfpSourceHLSound.getSelected();
    }
    
    /**
     * Удалить запись из списка подсветок
     */
    private void removeRecordHL(ListView <HighlightElement> lv)
    {
        HighlightElement hle = lv.getSelectionModel().getSelectedItem();
        if (hle == null)
            return;
        
        lv.getItems().remove(hle);
    }
    
    /**
     * Добавить запись в список подсветок
     */
    private void addRecordHL(ListView <HighlightElement> lv)
    {
        lv.getItems().add(new HighlightElement());
        lv.getSelectionModel().select(lv.getItems().size() - 1);
    }
    
    /**
     * нажать кнопку Выбрать звук в списоке подсветок
     */
    private void openHLSound(SelectFilePane sfp)
    {
        
        String fname = FileFunc.showOpenFileDialog(scene.getWindow(),
                                                   MyWorkingDir + File.separator + "Sounds",
                                                   new FileChooser.ExtensionFilter("WAV файлы (*.wav)", "*.wav"),
                                                   new FileChooser.ExtensionFilter("звуковые файлы (*.*)", "*.*"));
        if (fname.isEmpty())
            return;
        
        sfp.setValue(fname);
    }
    
    /**
     * нажать кнопку Вернуть звук в списоке подсветок
     */
    private void returnHLSound(ListView <HighlightElement> lv, SelectFilePane sfp)
    {
        HighlightElement hle = lv.getSelectionModel().getSelectedItem();
        if (hle == null)
            return;
        
        sfp.setValue(hle.soundPath);
    }
    
    /**
     * нажать кнопку Проверить звук в списоке подсветок
     */
    private void checkHLSound(SelectFilePane sfp)
    {
        new SoundPlayer(sfp.getValue()).start();
    }
    
    /**
     * нажать кнопку Загрузить регексп из файла в списоке подсветок
     */
    private void loadRegexpHL(TextFieldPane tfp)
    {
        if (tfp.getParent().isDisable())
            return;
        
        String fname = FileFunc.showOpenFileDialog(scene.getWindow(),
                                                   MyWorkingDir,
                                                   new FileChooser.ExtensionFilter("текстовые файлы (*.txt)", "*.txt"));
        if (fname.isEmpty())
            return;
        
        String text = FileFunc.fileToString(fname, config.Encoding);
        
        if (text == null || text.isEmpty())
            return;
        
        text = ".*" + text.replace("\r", "").replace("\n", ".*|.*") + ".*";
        text = text.replace(".*|.*.*", ".*");
        
        tfp.setValue(text);
        
    }
    
}
