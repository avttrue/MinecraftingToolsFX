package mcru.MinecraftingTools;

import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.connection;
import static mcru.MinecraftingTools.ApplicationControl.statusListener;
import static mcru.MinecraftingTools.MyApplication.scene;

/*
* поток проверки статуса сервера и управления progressBar
*/
public class StatusChecker extends Thread
{
    private boolean running = true;
    private int ServerAccessCheckInterval = 1000;
    
    private Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    public StatusChecker(int interval)
    {
        this.ServerAccessCheckInterval = interval;
        statusListener.setIsConnected(false);
    }
    
    public final void run()
    {
        this.setName(String.format("StatusChecker (%1$d)", this.getId()));
        logger.log(Level.INFO, String.format("%1$s начал работу", this.getName()));
        while (this.running)
        {
            try
            {
                sleep(ServerAccessCheckInterval);
            }
            catch (InterruptedException ignored)
            {
            }
            
            if (scene != null && connection != null)
            {
                statusListener.setIsConnected(scene.isConnection(false));
                statusListener.setPacketCount(connection.packetCount);
            }
        }
        
        logger.log(Level.INFO, String.format("%1$s завершил работу", this.getName()));
    }
    
    public final synchronized void Stop()
    {
        logger.log(Level.INFO, String.format("%1$s получил команду останов", this.getName()));
        running = false;
    }
}
