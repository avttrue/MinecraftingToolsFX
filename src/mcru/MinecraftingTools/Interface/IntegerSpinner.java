package mcru.MinecraftingTools.Interface;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 * Спиннер с заголовком для указания параметров типа Integer
 */
public class IntegerSpinner extends GridPane
{
    private Spinner <Integer> spinner = new Spinner <>();
    
    /**
     * Спиннер с заголовком для указания параметров типа Integer
     * @param caption    Заголовок
     * @param extendText Текст дополнительного пояснения снизу (null - не добавлять)
     * @param min        минимальное значение
     * @param max        максимальное значение
     * @param value      значение поумолчанию
     */
    public IntegerSpinner(String caption, String extendText, int min, int max, int value)
    {
        setHgap(10);
        setVgap(1);
        setPadding(new Insets(0, 0, 0, 5));
        spinner.setEditable(true);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, value));
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.getEditor().textProperty().addListener((obs, oldval, newval) -> checkAndSetValue(newval));
        
        Label label = new Label(caption);
        GridPane.setHgrow(label, Priority.SOMETIMES);
        
        add(label, 0, 0);
        add(spinner, 1, 0);
        if (extendText != null)
            add(new Label(extendText), 0, 1, 2, 1);
    }
    
    /**
     * получить значение
     */
    public int getValue()
    {
        return spinner.getValue();
    }
    
    /**
     * проверка и установка значения при ручном вводе
     */
    private void checkAndSetValue(String newval)
    {
        SpinnerValueFactory <Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null)
        {
            StringConverter <Integer> converter = valueFactory.getConverter();
            if (converter != null)
            {
                try
                {
                    Integer val = converter.fromString(newval);
                    if (val != null)
                        valueFactory.setValue(val);
                    else
                        valueFactory.setValue(0);
                }
                catch (NumberFormatException e)
                {
                    spinner.getEditor().setText(converter.toString(valueFactory.getValue()));
                }
            }
        }
    }
}
