package mcru.MinecraftingTools.Interface;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * панель выбора цвета с заголовком и дополнительным пояснением
 */
public class ColorPane extends GridPane
{
    private ColorPicker colorPicker;
    
    /**
     * панель выбора цвета с заголовком и дополнительным пояснением
     * @param caption    Заголовок
     * @param extendText Текст дополнительного пояснения снизу (null - не добавлять)
     * @param value      значение поумолчанию
     */
    public ColorPane(String caption, String extendText, Color value)
    {
        setHgap(10);
        setVgap(1);
        setPadding(new Insets(0, 0, 0, 5));
        
        colorPicker = new ColorPicker(value);
        
        Label label = new Label(caption);
        GridPane.setHgrow(label, Priority.SOMETIMES);
        
        add(label, 0, 0);
        add(colorPicker, 1, 0);
        if (extendText != null)
            add(new Label(extendText), 0, 1, 2, 1);
    }
    
    /**
     * получить значение
     */
    public Color getValue()
    {
        return colorPicker.getValue();
    }
    
    /**
     * установить значение
     */
    public void setValue(Color color)
    {
        colorPicker.setValue(color);
    }
}
