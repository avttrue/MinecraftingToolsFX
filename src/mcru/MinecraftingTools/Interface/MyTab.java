package mcru.MinecraftingTools.Interface;

import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.ContentFilter;
import mcru.MinecraftingTools.Helpers.StyleManager;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Вкладка на панели содержания {@link mcru.MinecraftingTools.MainScene#tabpaneContent}
 */
public class MyTab extends Tab
{
    private ContentFilter filter;
    
    public MyTab(String caption)
    {
        super();
        this.setText(caption);
        applyStyle(false);
    }
    
    public ContentFilter getFilter()
    {
        return filter;
    }
    
    public void setFilter(ContentFilter filter)
    {
        this.filter = filter;
    }
    
    public void applyStyle(boolean isHighlighted)
    {
        if (isHighlighted)
        {
            Color color = Color.web(config.NewMessageTabWebColor);
            this.setStyle(String.format("-fx-background-color: linear-gradient(#%1$s, #%2$s);",
                                        TextFunc.ColorToRGBCode(color.brighter()),
                                        TextFunc.ColorToRGBCode(color.darker())));
        }
        else
        {
            if (scene != null)
                this.setStyle(StyleManager.getBaseStyle());
        }
    }
    
}
