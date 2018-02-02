package mcru.MinecraftingTools.Helpers;

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.web.WebView;
import mcru.MinecraftingTools.MyApplication;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Functions.DesktopFunc.exploreFile;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Выполнение java из HTML
 * // http://knowles.co.za/javafx-and-javascript-calling-java-from-javascript/
 */
public class JSBridge
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    public boolean toListenMouseMove = true;
    
    /**
     * Обработка внутренних ссылок
     * @param key   ключ-идентификатор: [file | url | channel | player]
     * @param value значение ключа: либо путь до файла / урл, либо ID канала / игрока
     */
    public void linkMouseClick(String key, String value)
    {
        scene.getWindow().requestFocus();
        logger.log(Level.INFO,
                   String.format("Перехвачен клик по ссылке {%1$s, %2$s}, CTRL: %3$s",
                                 key,
                                 value,
                                 String.valueOf(CTRLisPressed)));
        try
        {
            switch (key)
            {
                case "file":
                    exploreFile(new File(value));
                    break;
                case "url":
                    scene.openWebLink(value);
                    break;
                case "channelID":
                    scene.openChannelLink(Long.valueOf(value));
                    break;
                case "playerID":
                    scene.openPlayerLinkByID(Long.valueOf(value));
                    break;
                case "playerUUID":
                    scene.openPlayerLinkByUUID(value);
                    break;
                case "MCRUDB-DeleteRecord":
                    if (minecraftingProfiles.remove(value))
                        Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(String.format(
                                "Из локальной базы Minecrafting.ru успешно удалена запись с UUID = %s",
                                value), LogMessage.MESSAGE_SUCCESS)));
                    else
                        Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(String.format(
                                "Из локальной базы Minecrafting.ru удалить запись с UUID = %s не удалось (была удалена ранее?)",
                                value), LogMessage.MESSAGE_ERROR)));
                    
                    break;
                case "MojangDB-DeleteRecord":
                    if (mojangProfiles.remove(value))
                        Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(String.format(
                                "Из локальной базы Mojang успешно удалена запись с UUID = %s",
                                value), LogMessage.MESSAGE_SUCCESS)));
                    else
                        Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(String.format(
                                "Из локальной базы Mojang удалить запись с UUID = %s не удалось (была удалена ранее?)",
                                value), LogMessage.MESSAGE_ERROR)));
                    break;
                default:
                {
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Ошибка при обработке ссылки", e);
        }
    }
    
    /**
     * Отобразить подсказку к ссылке
     * @param key   ключ-идентификатор: [file | url | channel | player]
     * @param value значение ключа: либо путь до файла / урл, либо ID канала / игрока
     */
    public void linkMouseOver(String key, String value)
    {
        if (!toListenMouseMove)
            return;
        
        if (!CTRLisPressed)
            return;
        
        logger.log(Level.INFO, String.format("Перехвачено наведение на ссылку + CTRL: {%1$s, %2$s}", key, value));
        
        String text;
        switch (key)
        {
            case "channelID":
                text = scene.getChannelFoolToolTip(Long.valueOf(value));
                break;
            
            case "playerID":
                text = scene.getPlayerFoolToolTip(Long.valueOf(value));
                break;
            
            default:
                text = "";
        }
        
        scene.CTRL_Tooltip.autoHideProperty().setValue(false);
        scene.CTRL_Tooltip.setStyle("-fx-font-family: monospace");
        scene.CTRL_Tooltip.setText(text);
        
        WebView wv_content = scene.getCurrentChatContentPane().getContent();
        
        if (wv_content != null)
            Tooltip.install(wv_content, scene.CTRL_Tooltip);
    }
    
    /**
     * Скрыть подсказку
     */
    public void linkMouseOut()
    {
        if (!toListenMouseMove)
            return;
        
        WebView wv_content = scene.getCurrentChatContentPane().getContent();
        
        if (wv_content != null)
            Tooltip.uninstall(wv_content, scene.CTRL_Tooltip);
    }
}
