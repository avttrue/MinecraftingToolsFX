package mcru.MinecraftingTools.Interface;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * Разделитель горизонтальный: отодвигает элементы после себя к правому краю
 */
public class HorizontalSpacer extends Pane
{
    public HorizontalSpacer()
    {
        super();
        HBox.setHgrow(this, Priority.SOMETIMES);
    }
}
