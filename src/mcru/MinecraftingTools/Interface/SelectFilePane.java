package mcru.MinecraftingTools.Interface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.MyApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Панель с чекбоксом-заголовком, текстовым полем и 3-мя кнопками
 * * String, EventHandler, EventHandler, EventHandler)}
 */
public class SelectFilePane extends BorderPane
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    private CheckBox checkBox;
    private TextField textField;
    
    /**
     * Панель с чекбоксом-заголовком, текстовым полем и 3-мя кнопками
     * @param checkText         текст чекбокса, если NULL - отсутствует
     * @param isChecked         значение чекбокса
     * @param defValue          значение в текстовом поле
     * @param eventButtonOpen   событие клика по кнопке выбора, если NULL - кнопка отсутствует
     * @param eventButtonReturn событие клика по кнопке возврата прежнего значения, если NULL - кнопка отсутствует
     * @param eventButtonCheck  событие клика по кнопке проверки-применения, если NULL - кнопка отсутствует
     */
    public SelectFilePane(
            String checkText, boolean isChecked, String defValue, EventHandler <ActionEvent> eventButtonOpen,
            EventHandler <ActionEvent> eventButtonReturn, EventHandler <ActionEvent> eventButtonCheck)
    {
        super();
        
        if (checkText != null)
        {
            checkBox = new CheckBox(checkText);
            checkBox.setSelected(isChecked);
            setTop(checkBox);
        }
        
        textField = new TextField(defValue);
        textField.setEditable(false);
        setCenter(textField);
        
        ToolBar toolBar = new ToolBar();
        setBottom(toolBar);
        
        if (eventButtonOpen != null)
        {
            Button buttonOpen = new Button("", new ImageView(ResFunc.getImage("open_catalog16")));
            buttonOpen.setTooltip(new Tooltip("Выбрать"));
            buttonOpen.setOnAction(eventButtonOpen);
            toolBar.getItems().add(buttonOpen);
        }
        
        if (eventButtonReturn != null)
        {
            Button buttonReturn = new Button("", new ImageView(ResFunc.getImage("update16")));
            buttonReturn.setTooltip(new Tooltip("Вернуть"));
            buttonReturn.setOnAction(eventButtonReturn);
            toolBar.getItems().add(buttonReturn);
        }
        
        if (eventButtonCheck != null)
        {
            Button buttonCheck = new Button("", new ImageView(ResFunc.getImage("event16")));
            buttonCheck.setTooltip(new Tooltip("Применить"));
            buttonCheck.setOnAction(eventButtonCheck);
            toolBar.getItems().add(buttonCheck);
        }
        
        getStyleClass().add("GROUP-BOX");
        setMargin(textField, new Insets(6, 1, 1, 1));
        setPrefWidth(Integer.MAX_VALUE);
    }
    
    /**
     * получить значение чекбокса
     */
    public boolean getSelected()
    {
        if (checkBox == null)
        {
            logger.log(Level.SEVERE, "getSelected() не применим в текущем контексте!");
            return false;
        }
        return checkBox.isSelected();
    }
    
    /**
     * установить значение чексбокса
     */
    public void setSelected(boolean value)
    {
        checkBox.setSelected(value);
    }
    
    /**
     * получить значение текстового поля
     */
    public String getValue()
    {
        return textField.getText();
    }
    
    /**
     * установить значение текстового поля
     */
    public void setValue(String value)
    {
        textField.setText(value);
    }
}
