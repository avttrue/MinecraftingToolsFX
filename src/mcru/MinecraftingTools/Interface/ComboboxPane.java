package mcru.MinecraftingTools.Interface;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import mcru.MinecraftingTools.Functions.TextFunc;

/**
 * панель выбора значения из выпадающего списка с заголовком и дополнительным пояснением
 */
public class ComboboxPane extends GridPane
{
    private ComboBox <String> comboBox;
    
    /**
     * панель выбора значения из выпадающего списка с заголовком и дополнительным пояснением
     * @param caption    Заголовок
     * @param extendText Текст дополнительного пояснения снизу (null - не добавлять)
     * @param value      значение поумолчанию
     * @param values     набор возможных значений
     */
    public ComboboxPane(String caption, String extendText, String[] values, String value)
    {
        setHgap(10);
        setVgap(1);
        setPadding(new Insets(0, 0, 0, 5));
        
        comboBox = new ComboBox <>();
        comboBox.setEditable(false);
        comboBox.setPlaceholder(new Label(TextFunc.GetSpace(40, ' ')));
        comboBox.getItems().setAll(values);
        comboBox.getSelectionModel().select(value);
        
        Label label = new Label(caption);
        GridPane.setHgrow(label, Priority.SOMETIMES);
        
        add(label, 0, 0);
        add(comboBox, 1, 0);
        if (extendText != null)
            add(new Label(extendText), 0, 1, 2, 1);
    }
    
    public String getValue()
    {
        return comboBox.getValue();
    }
}
