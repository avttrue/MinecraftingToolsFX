package mcru.MinecraftingTools.Dialogs;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mcru.MinecraftingTools.Functions.JsonFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.ContentElement;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Interface.GroupBox;
import mcru.MinecraftingTools.Interface.HorizontalSpacer;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationList;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MinecraftingAPI.TokenFunc.generateTokenByUUID;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Диалог управления аутентификационными данными
 */
public class EditAuthentificationDialog
{
    private ListView <AuthentificationData> listAuth = new ListView <>();
    private Label labelServerName = new Label();
    private Label labelUserName = new Label();
    private Label labelLastUpdate = new Label();
    private Label labelLocalToken = new Label();
    private Label labelAuthServerData = new Label();
    private Label labelAuthUserToken = new Label();
    private Button buttonDelete = new Button("", new ImageView(ResFunc.getImage("delete16")));
    private Button buttonClear = new Button("", new ImageView(ResFunc.getImage("clear16")));
    
    public EditAuthentificationDialog(Window parent)
    {
        Dialog dialog = new Dialog <>();
        dialog.initOwner(parent);
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle("Управление аутентификационными данными");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefSize(config.ConfigWindowWidth, config.ConfigWindowHeight);
        dialog.setGraphic(null);
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        ((Button) dialog.getDialogPane().lookupButton(okButtonType)).setOnAction(lambda -> clickOk());
        
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setGraphic(new ImageView(ResFunc.getImage("cancel24")));
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction(lambda -> {
        });
        
        AuthentificationList authentificationList = new AuthentificationList();
        Object o = JsonFunc.loadObjectFormFile(AuthentificationPath, authentificationList);
        
        if (o instanceof AuthentificationList)
            authentificationList = (AuthentificationList) o;
        
        listAuth.getItems().addAll(authentificationList.getList());
        listAuth.getSelectionModel().
                selectedItemProperty().
                        addListener((ObservableValue <? extends AuthentificationData> ov, AuthentificationData old_val, AuthentificationData new_val) -> selectListItem(
                                new_val));
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(1);
        grid.getStyleClass().add("-fx-font-family: monospace");
        grid.setPadding(new Insets(5, 5, 5, 5));
        
        grid.add(new Label("Сервер:"), 0, 0);
        grid.add(labelServerName, 1, 0);
        
        grid.add(new Label("Логин:"), 0, 1);
        grid.add(labelUserName, 1, 1);
        
        grid.add(new Label("Изменялся:"), 0, 2);
        grid.add(labelLastUpdate, 1, 2);
        
        grid.add(new Separator(), 0, 3);
        grid.add(new Separator(), 1, 3);
        
        grid.add(new Label("LocalToken:"), 0, 4);
        grid.add(labelLocalToken, 1, 4);
        
        grid.add(new Label("Session:"), 0, 5);
        grid.add(labelAuthServerData, 1, 5);
        
        grid.add(new Label("UserToken:"), 0, 6);
        grid.add(labelAuthUserToken, 1, 6);
        
        ToolBar buttonBar = new ToolBar();
        
        buttonDelete.setFocusTraversable(false);
        buttonDelete.setOnAction(lambda -> listAuth.getItems().remove(listAuth.getSelectionModel().getSelectedItem()));
        buttonDelete.setTooltip(new Tooltip("Удалить запись"));
        
        buttonClear.setFocusTraversable(false);
        buttonClear.setOnAction(lambda -> {
            AuthentificationData a = listAuth.getSelectionModel().getSelectedItem();
            a.setPassword("");
            a.setUserName("");
            a.setServerAuthInformation(null);
            a.setLocalToken(generateTokenByUUID(config.ServerTokenLength));
            selectListItem(a);
        });
        buttonClear.setTooltip(new Tooltip("Очистить данные"));
        
        buttonDelete.setDisable(true);
        buttonClear.setDisable(true);
        buttonBar.getItems().addAll(new HorizontalSpacer(), buttonDelete, buttonClear, new HorizontalSpacer());
        
        BorderPane paneContent = new BorderPane();
        paneContent.setBottom(buttonBar);
        paneContent.setCenter(new ScrollPane(grid));
        
        SplitPane spContent = new SplitPane(new GroupBox("Список аутентификаций", listAuth, true),
                                            new GroupBox("Значение", paneContent, true));
        spContent.setOrientation(Orientation.VERTICAL);
        
        dialog.getDialogPane().setContent(spContent);
        dialog.showAndWait();
    }
    
    private void selectListItem(AuthentificationData item)
    {
        if (item == null)
        {
            labelServerName.setText("");
            labelUserName.setText("");
            labelLastUpdate.setText("");
            labelLocalToken.setText("");
            labelAuthServerData.setText("");
            labelAuthUserToken.setText("");
            buttonDelete.setDisable(true);
            buttonClear.setDisable(true);
            return;
        }
        
        buttonDelete.setDisable(false);
        buttonClear.setDisable(false);
        labelServerName.setText(item.getServerName());
        labelUserName.setText(item.getUserName().isEmpty() ? notFound : item.getUserName());
        labelLastUpdate.setText(TextFunc.DateTimeToString(item.getLastUpdate()));
        labelLocalToken.setText(item.getLocalToken());
        
        if (item.getServerAuthInformation() != null)
        {
            if (item.getServerAuthInformation().UserToken != null)
            {
                StringBuilder s = new StringBuilder();
                for (byte b : item.getServerAuthInformation().UserToken)
                {
                    s.append(String.valueOf(b)).append(" ");
                }
                labelAuthUserToken.setText(s.toString());
            }
            else
                labelAuthUserToken.setText(notFound);
            
            if (item.getServerAuthInformation().Session != null)
                labelAuthServerData.setText(item.getServerAuthInformation().Session);
            else
                labelAuthServerData.setText(notFound);
        }
        else
        {
            labelAuthUserToken.setText(notFound);
            labelAuthServerData.setText(notFound);
        }
    }
    
    private void clickOk()
    {
        AuthentificationList authentificationList = new AuthentificationList();
        
        String alBackup = AuthentificationPath + ".backup";
        
        if (JsonFunc.saveObjectToFile(alBackup, authentificationList))
            Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(new ContentElement(String.format(
                    "Создана резервная копия аутентификационных данных: \"%1$s\"",
                    alBackup)), LogMessage.MESSAGE_SUCCESS)));
        else
            Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(new ContentElement(String.format(
                    "Ошибка при создании резервной копии аутентификационных данных: \"%1$s\"",
                    alBackup)), LogMessage.MESSAGE_ERROR)));
        
        AuthentificationList al = new AuthentificationList();
        
        for (AuthentificationData a : listAuth.getItems())
        {
            al.addAuthentification(a);
        }
        
        if (JsonFunc.saveObjectToFile(AuthentificationPath, al))
            Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(new ContentElement(
                    "Аутентификационные данные изменены и сохранены успешно"), LogMessage.MESSAGE_SUCCESS)));
        else
            Platform.runLater(() -> scene.logContent
                    .addMessage(new LogMessage(new ContentElement("Ошибка при сохранении аутентификационных данных"),
                                               LogMessage.MESSAGE_ERROR)));
        
    }
}
