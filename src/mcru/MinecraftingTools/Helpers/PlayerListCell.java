package mcru.MinecraftingTools.Helpers;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Interface.HorizontalSpacer;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfile;
import ontando.minecrafting.remote_access.env.DataValue;

import java.util.Map;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.ApplicationControl.minecraftingProfiles;

/**
 * Отрисовка элемента списка игроков {@link mcru.MinecraftingTools.MainScene#listPlayers}
 */
public class PlayerListCell extends ListCell <PlayerListElement>
{
    @Override
    protected void updateItem(PlayerListElement item, boolean empty)
    {
        super.updateItem(item, empty);
        
        if (empty || item == null)
        {
            setText(null);
            setGraphic(null);
            return;
        }
        
        Label labelCaption = new Label(TextFunc.SetFixedSize(item.nick, config.ChannelPlayerListLength),
                                       new ImageView(ResFunc.getImage("led_red16")));
        Label labelStatus = new Label(" ", new ImageView(ResFunc.getImage("blue_user16")));
        Label labelMark = new Label("", new ImageView(ResFunc.getImage("red_bookmark16")));
        Label labelViolation = new Label("", new ImageView(ResFunc.getImage("yellow_bookmark16")));
        
        // флаг того, что игрок меньше указанного времени на сервере
        for (Map.Entry <String, DataValue> entry : item.getProperties().entrySet())
        {
            if (entry.getKey().equals("played_server"))
            {
                item.alert = (entry.getValue().asLong() <= config.PlayedServerAlertValue * 3600);
                break;
            }
        }
        labelMark.setVisible(item.alert);
        // флаг того, что у игрока были правонарушения
        labelViolation.setVisible(false);
        MinecraftingProfile mp = minecraftingProfiles.find(item.uuid);
        String violation = "Нет правонарушений";
        if (mp != null)
        {
            if (!mp.SanctionsList.isEmpty())
            {
                violation = String.format("Правонарушений: %1$d", mp.SanctionsList.size());
                labelViolation.setVisible(config.ShowPlayerViolationMark);
            }
        }
        else
        {
            labelViolation.setGraphic(new ImageView(ResFunc.getImage("blue_bookmark16")));
            violation = "Правонарушений: ?";
            labelViolation.setVisible(config.ShowPlayerViolationMark);
        }
        
        setTooltip(new Tooltip(String.format("Игрок:\n%1$s\n%2$s", item.toString().replace(" / ", "\n"), violation)));
        
        // online_flags
        if ((item.online_flags & 0x01) != 0) // онлайн ли вообще
        {
            labelCaption.setGraphic(new ImageView(ResFunc.getImage("led_blue16")));
            
            if ((item.online_flags & 0x02) != 0) //  на ваниле
                labelCaption.setGraphic(new ImageView(ResFunc.getImage("led_green16")));
            
            if ((item.online_flags & 0x04) != 0) //  invisible
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("white_user16")));
            
            if ((item.online_flags & 0x08) != 0) //  hide
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("eye16")));
            
            // status_flags
            if ((item.status_flags & 0x01) != 0) // afk
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("black_user16")));
            
            if ((item.status_flags & 0x02) != 0) //  guest
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("yellow_user16")));
            
            if ((item.status_flags & 0x04) != 0) //  freeze
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("red_user16")));
            
            if ((item.status_flags & 0x08) != 0) //  mute
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("red_user16")));
            
            if ((item.status_flags & 0x10) != 0) //  curse
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("green_user16")));
            
            if ((item.status_flags & 0x20) != 0) //  bane
                labelStatus.setGraphic(new ImageView(ResFunc.getImage("red_user16")));
        }
        
        HBox box = new HBox(labelStatus, labelCaption, new HorizontalSpacer(), labelViolation, labelMark);
        setGraphic(box);
    }
}
