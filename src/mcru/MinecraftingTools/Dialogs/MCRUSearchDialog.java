package mcru.MinecraftingTools.Dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.ApplicationControl;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Interface.MyBorder;
import ontando.minecrafting.remote_access.network.packet.data.PerformActionPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * диалог подключения к серверу
 */
public class MCRUSearchDialog
{
    private static ComboBox <String> cbNick;
    private static ComboBox <String> cbUuid;
    
    public MCRUSearchDialog()
    {
        Dialog dialog = new Dialog <>();
        dialog.initOwner(scene.getWindow());
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle("Поиск игрока на сервере Minecrafting.ru");
        dialog.setHeaderText(null);
        dialog.setGraphic(new ImageView(ResFunc.getImage("search48")));
        dialog.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
              .forEach(node -> ((Control) node).setMinHeight(Region.USE_PREF_SIZE));
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        cbNick = new ComboBox <>();
        cbNick.setEditable(true);
        cbNick.setPromptText(" ник игрока");
        cbNick.getItems().addAll(config.MCRUUserFindNickHistory);
        cbNick.setPlaceholder(new Label(TextFunc.GetSpace(ApplicationControl.config.ComboBoxPlaceHolderLength, ' ')));
        
        cbUuid = new ComboBox <>();
        cbUuid.setEditable(true);
        cbUuid.setPromptText(" UUID игрока");
        cbUuid.getItems().addAll(config.MCRUUserFindUuidHistory);
        cbUuid.setPlaceholder(new Label(TextFunc.GetSpace(ApplicationControl.config.ComboBoxPlaceHolderLength, ' ')));
        
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        ((Button) dialog.getDialogPane().lookupButton(okButtonType)).setOnAction(lambda -> clickOK());
        
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setGraphic(new ImageView(ResFunc.getImage("cancel24")));
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction(lambda -> {
        });
        
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 10, 5, 10));
        
        grid.add(new Label("Ник игрока:"), 0, 0);
        grid.add(cbNick, 1, 0);
        GridPane.setHgrow(cbNick, Priority.SOMETIMES);
        
        grid.add(new Label("UUID игрока:"), 0, 1);
        grid.add(cbUuid, 1, 1);
        GridPane.setHgrow(cbUuid, Priority.SOMETIMES);
        
        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }
    
    private static void clickOK()
    {
        String textNick = cbNick.getEditor().getText();
        String textUuid = cbUuid.getEditor().getText();
        
        if (textNick.isEmpty() && textUuid.isEmpty())
            return;
        
        Map <String, Object> options = new HashMap <>();
        
        if (!textNick.isEmpty())
        {
            config.MCRUUserFindNickHistory.remove(textNick);
            config.MCRUUserFindNickHistory.add(0, textNick);
            
            if (config.MCRUUserFindNickHistory.size() > config.SearchHistorySize)
                config.MCRUUserFindNickHistory =
                        new ArrayList <>(config.MCRUUserFindNickHistory.subList(0, config.SearchHistorySize));
            
            Platform.runLater(() -> scene.logContent
                    .addMessage(new LogMessage(String.format("Ищем игрока по нику \"%1$s\"...", textNick),
                                               LogMessage.MESSAGE_INFO)));
            options.put("name", textNick);
        }
        
        if (!textUuid.isEmpty())
        {
            config.MCRUUserFindUuidHistory.remove(textUuid);
            config.MCRUUserFindUuidHistory.add(0, textUuid);
            
            if (config.MCRUUserFindUuidHistory.size() > config.SearchHistorySize)
                config.MCRUUserFindUuidHistory =
                        new ArrayList <>(config.MCRUUserFindUuidHistory.subList(0, config.SearchHistorySize));
            
            try
            {
                Platform.runLater(() -> scene.logContent
                        .addMessage(new LogMessage(String.format("Ищем игрока по UUID \"%1$s\"...", textUuid),
                                                   LogMessage.MESSAGE_INFO)));
                options.put("uuid", UUID.fromString(textUuid));
            }
            catch (IllegalArgumentException e)
            {
                Platform.runLater(() -> scene.logContent
                        .addMessage(new LogMessage(String.format("Неверный формат UUID: \"%1$s\"...", textUuid),
                                                   LogMessage.MESSAGE_ERROR)));
            }
        }
        
        setStatusUserFind(true);
        
        PerformActionPacket packet = connection.clientEnvironmentManager.packAction(connection.userFindID, options);
        connection.client.sendPacket(packet);
    }
}
