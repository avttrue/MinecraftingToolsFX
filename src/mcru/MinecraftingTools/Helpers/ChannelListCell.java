package mcru.MinecraftingTools.Helpers;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Interface.HorizontalSpacer;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * Отрисовка элемента списка каналов {@link mcru.MinecraftingTools.MainScene#listChannels}
 */
public class ChannelListCell extends ListCell <ChannelListElement>
{
    @Override
    protected void updateItem(ChannelListElement item, boolean empty)
    {
        super.updateItem(item, empty);
        
        if (empty || item == null)
        {
            setText(null);
            setGraphic(null);
            return;
        }
        
        setTooltip(new Tooltip(String.format("Канал:\n%1$s", item.toString().replace(" / ", "\n"))));
        Label labelCaption = new Label();
        Label labelCount = new Label();
        
        labelCaption.setText(String.format("%1$s", TextFunc.SetFixedSize(item.name, config.ChannelPlayerListLength)));
        labelCount.setText(String.format("[%1$d]", item.playersID.size()));
        
        switch (item.type)
        {
            case "party":
                item.image = new ImageView(ResFunc.getImage("party16")).getImage();
                break;
            case "city":
                item.image = new ImageView(ResFunc.getImage("crown16")).getImage();
                break;
            case "default":
                item.image = new ImageView(ResFunc.getImage("star16")).getImage();
                break;
            default:
                item.image = new ImageView(ResFunc.getImage("white_star16")).getImage();
        }
        
        labelCaption.setGraphic(new ImageView(item.image));
        
        HBox box = new HBox(labelCaption, new HorizontalSpacer(), labelCount);
        setGraphic(box);
    }
}
