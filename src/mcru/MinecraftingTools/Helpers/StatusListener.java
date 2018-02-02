package mcru.MinecraftingTools.Helpers;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.image.ImageView;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Sound.SoundPlayer;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * слушатель состояний связи с сервером
 */
public class StatusListener
{
    /**
     * отображение количества принятых пакетов
     */
    private LongProperty PacketCount = new SimpleLongProperty();
    /**
     * отображение состояния соединения
     */
    private BooleanProperty IsConnected = new SimpleBooleanProperty();
    /**
     * отображение прогрессбара
     */
    private BooleanProperty ShowProgressBar = new SimpleBooleanProperty();
    
    /**
     * слушатель состояний связи с сервером
     */
    public StatusListener()
    {
        IsConnectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Runnable SendDataToApplication = () -> {
                if (newValue)
                {
                    scene.labelServerStatus.setGraphic(new ImageView(ResFunc.getImage("led_green16")));
                    scene.logContent.addMessage(new LogMessage("Соединение с сервером установлено",
                                                               LogMessage.MESSAGE_SUCCESS));
                }
                else
                {
                    scene.labelServerStatus.setGraphic(new ImageView(ResFunc.getImage("led_red16")));
                    scene.logContent
                            .addMessage(new LogMessage("Соединение с сервером потеряно", LogMessage.MESSAGE_ERROR));
                    
                    if (config.PlayAlarmDisconnectSound)
                        new SoundPlayer(config.AlarmDisconnectSoundFile).start();
                }
                scene.paneStatus.requestLayout();
            };
            Platform.runLater(SendDataToApplication);
        });
        
        PacketCountProperty().addListener((observableValue, oldValue, newValue) -> Platform
                .runLater(() -> scene.labelPacketCount.setText(String.valueOf(newValue))));
        
        ShowProgressBarProperty().addListener((observableValue, oldValue, newValue) -> Platform
                .runLater(() -> scene.progressBar.setVisible(newValue)));
    }
    
    public final Long getPacketCount() {return PacketCount.get();}
    
    public final void setPacketCount(Long value) {PacketCount.set(value);}
    
    public LongProperty PacketCountProperty() {return PacketCount;}
    
    public final boolean getAIsConnected() {return IsConnected.get();}
    
    public final void setIsConnected(boolean value) {IsConnected.set(value);}
    
    public BooleanProperty IsConnectedProperty() {return IsConnected;}
    
    public final boolean getShowProgressBar() {return ShowProgressBar.get();}
    
    public final void setShowProgressBar(boolean value) {ShowProgressBar.set(value);}
    
    public BooleanProperty ShowProgressBarProperty() {return ShowProgressBar;}
}
