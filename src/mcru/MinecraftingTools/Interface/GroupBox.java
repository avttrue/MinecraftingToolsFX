package mcru.MinecraftingTools.Interface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Панель с заголовком
 */
public class GroupBox extends BorderPane
{
    public GroupBox(String Caption, Node content, boolean maxHeight)
    {
        super();
        
        Label title = new Label(Caption);
        title.setAlignment(Pos.CENTER);
        title.getStyleClass().add("GROUP-BOX-CAPTION");
        title.setPrefWidth(Integer.MAX_VALUE);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        getStyleClass().add("GROUP-BOX");
        setMargin(content, new Insets(6, 1, 1, 1));
        setPrefWidth(Integer.MAX_VALUE);
        
        if (maxHeight)
            setPrefHeight(Integer.MAX_VALUE);
        
        setTop(title);
        setCenter(content);
    }
}
