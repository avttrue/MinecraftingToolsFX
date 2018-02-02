package mcru.MinecraftingTools;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.Functions.ResFunc;

import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.NAME;
import static mcru.MinecraftingTools.ApplicationControl.VERSION;

public class MyPreloader extends Preloader
{
    
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    private Stage preloaderStage;
    private Scene scene;
    private boolean isActive = true;
    
    @Override
    public void init() throws Exception
    {
        Platform.runLater(() -> {
            Pane root = new Pane();
            root.setStyle("-fx-background-color:transparent;");
            
            Image image = ResFunc.getImage("mainicon512");
            assert image != null;
            scene = new Scene(root, image.getWidth(), image.getHeight());
            ImagePattern pattern = new ImagePattern(image);
            scene.setFill(Color.TRANSPARENT);
            scene.setFill(pattern);
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                preloaderStage.hide();
                isActive = false;
                logger.log(Level.INFO, "Отображение стартовой заставки выключено");
            });
        });
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        preloaderStage = primaryStage;
        preloaderStage.setTitle(String.format("%1$s %2$s", NAME, VERSION));
        preloaderStage.setAlwaysOnTop(true);
        preloaderStage.setScene(scene);
        preloaderStage.initStyle(StageStyle.TRANSPARENT);
        preloaderStage.show();
    }
    
    @Override
    public void handleApplicationNotification(PreloaderNotification info)
    {
        if (!isActive)
            return;
        
        if (info instanceof ProgressNotification)
        {
            try
            {
                Thread.sleep(1);
                preloaderStage.setOpacity(((ProgressNotification) info).getProgress());
            }
            catch (InterruptedException ignored)
            {
            }
        }
    }
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification info)
    {
        StateChangeNotification.Type type = info.getType();
        switch (type)
        {
            case BEFORE_LOAD:
                
                break;
            case BEFORE_INIT:
                
                break;
            case BEFORE_START:
                
                preloaderStage.hide();
                break;
        }
    }
}
