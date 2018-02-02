package mcru.MinecraftingTools.Interface;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * панель текстового поля с заголовком
 */
public class TextFieldPane extends GridPane
{
    private TextField textField = new TextField();
    
    public TextFieldPane(String caption, String value)
    {
        setHgap(10);
        setVgap(1);
        setPadding(new Insets(0, 0, 0, 5));
        
        textField.setText(value);
        
        Label label = new Label(caption);
        GridPane.setHgrow(textField, Priority.SOMETIMES);
        add(label, 0, 0);
        add(textField, 1, 0);
    }
    
    /**
     * получить значение
     */
    public String getValue()
    {
        return textField.getText();
    }
    
    /**
     * установить значение
     */
    public void setValue(String value)
    {
        textField.setText(value);
    }
}
