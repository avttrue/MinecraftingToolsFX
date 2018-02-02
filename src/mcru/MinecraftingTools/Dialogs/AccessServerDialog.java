package mcru.MinecraftingTools.Dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mcru.MinecraftingTools.ApplicationControl;
import mcru.MinecraftingTools.Functions.JsonFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationList;

import static mcru.MinecraftingTools.ApplicationControl.AuthentificationPath;
import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;

/**
 * диалог аутентификации на сервере
 */
public class AccessServerDialog
{
    private Dialog <AuthentificationData> dialog;
    private ButtonType okButtonType;
    private ComboBox <AuthentificationData> cbServerAddress;
    private TextField textUserName;
    private PasswordField textUserPass;
    private CheckBox cbRememberPass;
    private AuthentificationList authentificationList;
    
    public AccessServerDialog(Window parent)
    {
        dialog = new Dialog <>();
        dialog.initOwner(parent);
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle("Подключение к серверу");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
              .forEach(node -> ((Control) node).setMinHeight(Region.USE_PREF_SIZE));
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        dialog.setGraphic(new ImageView(ResFunc.getImage("lock48")));
        
        okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setGraphic(new ImageView(ResFunc.getImage("cancel24")));
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction(lambda -> {
        });
        
        authentificationList = new AuthentificationList();
        Object o = JsonFunc.loadObjectFormFile(AuthentificationPath, authentificationList);
        if (o instanceof AuthentificationList)
            authentificationList = (AuthentificationList) o;
        
        textUserName = new TextField();
        textUserPass = new PasswordField();
        
        cbServerAddress = new ComboBox <>();
        cbServerAddress.setEditable(true);
        cbServerAddress.setPromptText("[адрес сервера]:[порт сервера]");
        cbServerAddress.getItems().addAll(authentificationList.getList());
        cbServerAddress
                .setPlaceholder(new Label(TextFunc.GetSpace(ApplicationControl.config.ComboBoxPlaceHolderLength, ' ')));
        
        if (cbServerAddress.getItems().size() > 0)
        {
            cbServerAddress.getSelectionModel().select(cbServerAddress.getItems().size() - 1);
            textUserName.setText(cbServerAddress.getSelectionModel().getSelectedItem().getUserName());
            textUserPass.setText(cbServerAddress.getSelectionModel().getSelectedItem().getPassword());
        }
        
        cbServerAddress.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null)
                return;
            
            textUserName.setText(newValue.getUserName());
            textUserPass.setText(newValue.getPassword());
        });
        
        cbRememberPass = new CheckBox("Запомнить пароль (не безопасно)");
        cbRememberPass.setSelected(config.RememberPassword);
        
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 10, 5, 10));
        
        grid.add(new Label("Сервер:"), 0, 0);
        grid.add(cbServerAddress, 1, 0);
        GridPane.setHgrow(cbServerAddress, Priority.SOMETIMES);
        
        grid.add(new Label("Логин:"), 0, 1);
        grid.add(textUserName, 1, 1);
        
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(textUserPass, 1, 2);
        
        grid.add(cbRememberPass, 1, 3);
        
        dialog.setResultConverter(this::clickButton);
        dialog.getDialogPane().setContent(grid);
    }
    
    /**
     * обработка клика по кнопке диалога
     * @param dialogButton тип кликнутой кнопки
     */
    private AuthentificationData clickButton(ButtonType dialogButton)
    {
        if (dialogButton != okButtonType)
            return null;
        
        String address = "";
        String port = "";
        String addressandport = cbServerAddress.getEditor().getText();
        String login = textUserName.getText();
        String password = textUserPass.getText();
        
        config.RememberPassword = cbRememberPass.isSelected();
        
        if (addressandport.lastIndexOf(":") > -1)
        {
            address = addressandport.substring(0, addressandport.lastIndexOf(":"));
            port = addressandport.substring(addressandport.lastIndexOf(":") + 1, addressandport.length());
        }
        
        if (TextFunc.StringIsInteger(port) && !address.isEmpty() && !login.isEmpty() && !password.isEmpty())
        {
            AuthentificationData ad = authentificationList.getAuthentification(addressandport);
            ad.setServerName(addressandport);
            ad.setPassword(password);
            ad.setUserName(login);
            
            return ad;
        }
        
        CommonDialogs.ShowMessage(Alert.AlertType.ERROR,
                                  "Ошибка",
                                  null,
                                  String.format(
                                          "Указаны некорректные данные!\nСервер = %1$s\nПользователь = %2$s\nПароль = %3$s",
                                          addressandport,
                                          login,
                                          password),
                                  null);
        
        return null;
    }
    
    /**
     * отобразить диалог
     */
    public AuthentificationData show()
    {
        return dialog.showAndWait().orElse(null);
    }
}
