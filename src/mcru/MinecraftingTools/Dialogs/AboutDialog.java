package mcru.MinecraftingTools.Dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.Functions.DesktopFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Interface.MyBorder;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Окно информации о приложении
 */
public class AboutDialog
{
    public AboutDialog()
    {
        Dialog dialog = new Dialog <>();
        dialog.initOwner(scene.getWindow());
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle("Об этом");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefSize(800, 800);
        dialog.setGraphic(null);
        
        TabPane tabPane = new TabPane();
        
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        Hyperlink hlMCRU = new Hyperlink("minecrafting.ru");
        hlMCRU.setFocusTraversable(false);
        hlMCRU.setOnAction(lambda -> DesktopFunc.openWebResource(MinecraftingURL, false));
        
        VBox boxMainInfo = new VBox(3);
        boxMainInfo.setAlignment(Pos.CENTER);
        boxMainInfo.setBorder(new Border(new MyBorder(5, 1)));
        boxMainInfo.getChildren()
                   .addAll(new Label(NAME + " " + VERSION), new ImageView(ResFunc.getImage("mainicon512")), hlMCRU);
        
        TextArea taAddInfo = new TextArea();
        taAddInfo.setWrapText(true);
        taAddInfo.setEditable(false);
        taAddInfo.setBorder(new Border(new MyBorder(5, 1)));
        
        taAddInfo.setText("Система:\n" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " +
                          System.getProperty("os.arch") + "\n");
        taAddInfo.appendText("Java:\n" + System.getProperty("java.runtime.version") + "\n");
        taAddInfo.appendText("Кодировка:\n\"" + System.getProperty("file.encoding") + "\"\n");
        taAddInfo.appendText("Рабочий каталог:\n" + MyWorkingDir + "\n");
        taAddInfo.appendText("Каталог логов чата:\n" + LogDirectory + "\n");
        taAddInfo.appendText("Каталог для шаблонов:\n" + PresetsDirectory + "\n");
        taAddInfo.appendText("Файл настроек:\n" + ConfigFilePath + "\n");
        taAddInfo.appendText("Файл аутентификаций:\n" + AuthentificationPath + "\n");
        taAddInfo.appendText("Локальная база данных игроков Minecrafting.ru:\n" + PathMinecraftingProfiles + "\n");
        taAddInfo.appendText("Локальная база данных игроков Mojang API:\n" + PathMojangProfiles + "\n");
        
        if (LogChatFile != null)
            taAddInfo.appendText("Файл логов чата:\n" + LogChatFile + "\n");
        
        if (LogApplicationFile != null)
            taAddInfo.appendText("Файл логов приложения:\n" + LogApplicationFile + "\n");
        
        
        Tab tapMainInfo = new Tab("Общая", boxMainInfo);
        tapMainInfo.setClosable(false);
        
        Tab tapAddInfo = new Tab("Дополнительно", taAddInfo);
        tapAddInfo.setClosable(false);
        
        tabPane.getTabs().addAll(tapMainInfo, tapAddInfo);
        
        dialog.getDialogPane().setContent(tabPane);
        dialog.show();
    }
}
