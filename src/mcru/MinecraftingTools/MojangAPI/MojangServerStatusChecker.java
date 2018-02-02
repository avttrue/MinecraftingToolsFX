package mcru.MinecraftingTools.MojangAPI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.Functions.DesktopFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.MyApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.ApplicationControl.setStatusMojangServerStatusChecker;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Проверка статуса серверов Можанг <p>
 * см.: http://wiki.vg/Mojang_API
 */
public class MojangServerStatusChecker extends Thread
{
    private String urlServerStatus = config.MojangApiServerStatus;
    private String dataServerStatus = "";
    private Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    public final void run()
    {
        this.setName(String.format("MojangServerStatusChecker (%1$d)", this.getId()));
        logger.log(Level.INFO, String.format("%1$s начал работу", this.getName()));
        
        ReadServerStatus();
        
        // отправляем данные в основную форму
        Runnable SendDataToApplication = () -> {
            if (dataServerStatus == null || dataServerStatus.isEmpty())
            {
                scene.logContent.addMessage(new LogMessage(
                        "Данные статусов серверов Mojang получить не удалось, попробуйте позже",
                        LogMessage.MESSAGE_ERROR));
            }
            else
            {
                scene.logContent.addMessage(new LogMessage("Получены данные статусов серверов Mojang",
                                                           LogMessage.MESSAGE_SUCCESS));
                
                javafx.scene.control.Dialog dialog = new javafx.scene.control.Dialog <>();
                dialog.initOwner(scene.getWindow());
                dialog.setTitle("Статусы серверов Mojang");
                checkAndLoadCSS(dialog.getDialogPane());
                dialog.initModality(Modality.NONE);
                dialog.initStyle(StageStyle.UTILITY);
                dialog.setResizable(true);
                dialog.setHeaderText(null);
                dialog.getDialogPane().setPrefSize(600, 600);
                dialog.setGraphic(null);
                
                ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
                
                ((javafx.scene.control.Button) dialog.getDialogPane().lookupButton(okButtonType))
                        .setGraphic(new ImageView(ResFunc.getImage("OK24")));
                
                dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
                
                TextFlow tfContent = new TextFlow();
                try
                {
                    Gson gson = new Gson();
                    Type type = new TypeToken <List <Map <String, String>>>()
                    {}.getType();
                    java.util.List <Map <String, String>> list = gson.fromJson(dataServerStatus, type);
                    
                    for (Map <String, String> mss : list)
                    {
                        for (Map.Entry <String, String> entry : mss.entrySet())
                        {
                            switch (entry.getValue())
                            {
                                case "green":
                                    tfContent.getChildren().add(new ImageView(ResFunc.getImage("led_green16")));
                                    break;
                                case "red":
                                    tfContent.getChildren().add(new ImageView(ResFunc.getImage("led_red16")));
                                    break;
                                case "yellow":
                                    tfContent.getChildren().add(new ImageView(ResFunc.getImage("led_yellow16")));
                                    break;
                                default:
                                    tfContent.getChildren().add(new ImageView(ResFunc.getImage("led_grey16")));
                            }
                            tfContent.getChildren().add(new Text(String.format("\t%1$s\n", entry.getKey())));
                        }
                    }
                }
                catch (Exception e)
                {
                    scene.logContent.addMessage(new LogMessage(String.format(
                            "Данные статусов серверов Mojang получить не удалось, что-то пошло не так: %1$s\n",
                            e.getMessage()), LogMessage.MESSAGE_ERROR));
                    
                    logger.log(Level.SEVERE, "Данные статусов серверов Mojang получить не удалось", e);
                }
                tfContent.getChildren().add(new Text("\n\n"));
                Hyperlink hlWiki = new Hyperlink("http://wiki.vg/Mojang_API");
                hlWiki.setFocusTraversable(false);
                hlWiki.setOnAction(lambda -> {
                    DesktopFunc.openWebResource(hlWiki.getText(), false);
                    hlWiki.setVisited(false);
                });
                tfContent.getChildren().add(hlWiki);
                
                ScrollPane spPlayers = new javafx.scene.control.ScrollPane(tfContent);
                spPlayers.setBorder(new Border(new MyBorder(5, 1)));
                dialog.getDialogPane().setContent(spPlayers);
                dialog.show();
            }
            
            setStatusMojangServerStatusChecker(false);
        };
        
        Platform.runLater(SendDataToApplication);
        logger.log(Level.INFO, String.format("%1$s закончил работу", this.getName()));
    }
    
    /**
     * лезем на сайт и читаем данные по статусу серверов
     */
    private void ReadServerStatus()
    {
        try (InputStream inputstream = new URL(urlServerStatus).openStream())
        {
            logger.log(Level.INFO, String.format("Читаем статусы серверов по адресу: %1$s", urlServerStatus));
            
            dataServerStatus =
                    new BufferedReader(new InputStreamReader(inputstream)).lines().collect(Collectors.joining("\n"));
            
            logger.log(Level.INFO, String.format("Нашли: %1$s", dataServerStatus));
            
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка при чтении статуса серверов", e);
        }
    }
}
