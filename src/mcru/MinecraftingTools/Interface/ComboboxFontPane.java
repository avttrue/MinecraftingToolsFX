package mcru.MinecraftingTools.Interface;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.SysFunc;
import mcru.MinecraftingTools.Functions.TextFunc;

import static mcru.MinecraftingTools.ApplicationControl.config;

/**
 * панель выбора шрифта из выпадающего списка с заголовком и дополнительным пояснением
 */
public class ComboboxFontPane extends GridPane
{
    ComboBox <String> comboBox;
    
    /**
     * панель выбора шрифта из выпадающего списка с заголовком и дополнительным пояснением
     * @param caption    Заголовок
     * @param extendText Текст дополнительного пояснения снизу (null - не добавлять)
     * @param value      значение поумолчанию
     */
    public ComboboxFontPane(String caption, String extendText, String value)
    {
        setHgap(10);
        setVgap(1);
        setPadding(new Insets(0, 0, 0, 5));
        
        comboBox = new ComboBox <>();
        comboBox.setCellFactory(param -> new FontListCell());
        comboBox.setEditable(false);
        comboBox.setPlaceholder(new Label(TextFunc.GetSpace(40, ' ')));
        comboBox.getItems().setAll(SysFunc.getFontFamilies());
        if (SysFunc.checkFontFamily(value))
            comboBox.getSelectionModel().select(value);
        else
            comboBox.getSelectionModel().select(SysFunc.getDefaultFontName());
        
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

/**
 * Ячейка выпадающего списка
 */
class FontListCell extends ListCell <String>
{
    @Override
    protected void updateItem(String item, boolean empty)
    {
        super.updateItem(item, empty);
        
        if (empty || item == null)
            return;
        
        Label label = new Label(item, new ImageView(ResFunc.getImage("font24")));
        label.setFont(new Font(item, config.CommonFontSize + 2));
        setGraphic(label);
    }
}