package mcru.MinecraftingTools.Interface;

import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * Прозрачная рамка
 */
public class MyBorder extends BorderStroke
{
    public MyBorder(int width)
    {
        super(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(width));
    }
    
    public MyBorder(int radius, int width)
    {
        super(Color.web(config.ThemeWebColor).darker(),
              BorderStrokeStyle.SOLID,
              new CornerRadii(radius),
              new BorderWidths(width));
    }
    
    public MyBorder(int radius, int width, BorderStrokeStyle bss)
    {
        super(Color.web(config.ThemeWebColor).darker(), bss, new CornerRadii(radius), new BorderWidths(width));
    }
    
    public MyBorder(int radius, int width, BorderStrokeStyle bss, Color color)
    {
        super(color, bss, new CornerRadii(radius), new BorderWidths(width));
    }
}
