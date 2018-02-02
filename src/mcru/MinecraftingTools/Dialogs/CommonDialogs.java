package mcru.MinecraftingTools.Dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.MyApplication;

import java.util.Optional;

import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;

/**
 * сообщения и диалоги общего назначения
 */
public class CommonDialogs
{
    /**
     * Диалог вывода короткого сообщения
     * @param type тип сообщения в формате {@link Alert.AlertType}
     */
    public static void ShowMessage(Alert.AlertType type, String caption, String header, String message, ImageView icon)
    {
        Alert alert = new Alert(type);
        alert.initOwner(MyApplication.scene.getWindow());
        checkAndLoadCSS(alert.getDialogPane());
        alert.setTitle(caption);
        alert.setContentText(message);
        alert.setHeaderText(header);
        alert.initStyle(StageStyle.UTILITY);
        alert.setResizable(true);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
             .forEach(node -> ((Control) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        alert.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        if (icon != null)
            alert.setGraphic(icon);
        
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK))
                .setGraphic(new ImageView(ResFunc.getImage("yes24")));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("");
        
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("");
        
        alert.show();
    }
    
    /**
     * Диалог вывода объёмного сообщения
     * @param type тип сообщения в формате {@link Alert.AlertType}
     */
    public static void ShowLongMessage(Alert.AlertType type, String caption, String shortMessage, String longMessage)
    {
        Alert alert = new Alert(type);
        alert.initOwner(MyApplication.scene.getWindow());
        checkAndLoadCSS(alert.getDialogPane());
        alert.initStyle(StageStyle.UTILITY);
        alert.setResizable(true);
        alert.setTitle(caption);
        alert.setHeaderText(shortMessage);
        alert.setContentText(null);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
             .forEach(node -> ((Control) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        alert.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        alert.setGraphic(null);
        
        TextArea textArea = new TextArea(longMessage);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.positionCaret(0);
        
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        
        alert.getDialogPane().setContent(expContent);
        ButtonType copyButtonType = new ButtonType("");
        alert.getDialogPane().getButtonTypes().add(copyButtonType);
        
        Button copyBytton = (Button) alert.getDialogPane().lookupButton(copyButtonType);
        copyBytton.setGraphic(new ImageView(ResFunc.getImage("copy24")));
        copyBytton.setTooltip(new Tooltip("Копировать в буфер обмена"));
        copyBytton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(longMessage);
            clipboard.setContent(content);
        });
        
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK))
                .setGraphic(new ImageView(ResFunc.getImage("yes24")));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("");
        
        alert.show();
    }
    
    /**
     * Диалог запроса подтверждения
     */
    public static boolean ShowConfirmationDialog(String caption, String header, String message, ImageView icon)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(MyApplication.scene.getWindow());
        checkAndLoadCSS(alert.getDialogPane());
        alert.initStyle(StageStyle.UTILITY);
        alert.setResizable(true);
        alert.setTitle(caption);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
             .forEach(node -> ((Control) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        alert.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        if (icon != null)
            alert.setGraphic(icon);
        
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK))
                .setGraphic(new ImageView(ResFunc.getImage("yes24")));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("");
        
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setGraphic(new ImageView(ResFunc.getImage("cancel24")));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("");
        
        Optional <ButtonType> result = alert.showAndWait();
        return (result.isPresent()) && (result.get() == ButtonType.OK);
    }
}