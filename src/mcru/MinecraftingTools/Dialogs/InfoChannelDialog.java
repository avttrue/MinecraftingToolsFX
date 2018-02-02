package mcru.MinecraftingTools.Dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Helpers.ChannelListElement;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.Interface.MyBorder;
import ontando.minecrafting.remote_access.env.DataValue;

import java.util.Map;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Окно информации о канале
 */
public class InfoChannelDialog
{
    public InfoChannelDialog(ChannelListElement cle)
    {
        Dialog dialog = new Dialog <>();
        dialog.initOwner(scene.getWindow());
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle(cle.toString());
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefSize(config.InfoWindowWidth, config.InfoWindowHeight);
        dialog.setGraphic(null);
        
        TextArea textArea = new TextArea();
        TabPane tabPane = new TabPane();
        
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        ButtonType copyButtonType = new ButtonType("");
        
        dialog.getDialogPane().getButtonTypes().addAll(copyButtonType, okButtonType);
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        Button copyBytton = (Button) dialog.getDialogPane().lookupButton(copyButtonType);
        
        copyBytton.setGraphic(new ImageView(ResFunc.getImage("copy24")));
        copyBytton.setTooltip(new Tooltip("Копировать в буфер обмена"));
        copyBytton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(textArea.getText());
            clipboard.setContent(content);
        });
        
        // информация об игроках
        VBox vbPlayers = new VBox();
        for (long id : cle.playersID)
        {
            if (id < 0)
                continue;
            
            PlayerListElement ple = scene.getPlayerListElementByID(id);
            
            if (ple == null)
                continue;
            
            Hyperlink hlPlayer = new Hyperlink(ple.getNick());
            hlPlayer.setFocusTraversable(false);
            hlPlayer.setOnAction(event -> {
                scene.openPlayerLinkByID(id);
                hlPlayer.setVisited(false);
            });
            hlPlayer.setGraphic(new ImageView(ple.image));
            vbPlayers.getChildren().add(hlPlayer);
        }
        
        ScrollPane spPlayers = new ScrollPane(vbPlayers);
        spPlayers.setBorder(new Border(new MyBorder(5, 1)));
        
        // информация о канале
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setBorder(new Border(new MyBorder(5, 1)));
        
        // fields
        textArea.setText("FIELDS\n");
        for (Map.Entry <String, DataValue> entry : cle.fields.entrySet())
        {
            textArea.appendText(entry.getValue() + "\n");
        }
        
        // properties
        textArea.appendText("\nPROPERTIES\n");
        for (Map.Entry <String, DataValue> entry : cle.properties.entrySet())
        {
            textArea.appendText(entry.getValue() + "\n");
        }
        textArea.positionCaret(0);
        
        Tab tabPlayers = new Tab("Игроки", spPlayers);
        tabPlayers.setClosable(false);
        Tab tabInfo = new Tab("Информация", textArea);
        tabInfo.setClosable(false);
        
        tabPane.getTabs().addAll(tabPlayers, tabInfo);
        dialog.getDialogPane().setContent(tabPane);
        dialog.show();
    }
}
