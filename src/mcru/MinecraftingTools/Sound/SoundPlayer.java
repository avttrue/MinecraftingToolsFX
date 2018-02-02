package mcru.MinecraftingTools.Sound;

import javafx.application.Platform;
import mcru.MinecraftingTools.Helpers.ContentElement;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.MyApplication;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * класс для воспроизведения звуков
 * сделано по материалам
 * http://stackoverflow.com/questions/577724/trouble-playing-wav-in-java/577926#577926
 */
public class SoundPlayer extends Thread
{
    private Logger logger = Logger.getLogger(MyApplication.class.getName());
    private String soundFile = "";
    
    public SoundPlayer(String soundFile)
    {
        this.soundFile = soundFile;
        this.setName(String.format("SoundPlayer (%1$d)", this.getId()));
    }
    
    @Override
    public void run()
    {
        logger.log(Level.INFO,
                   String.format("%1$s приступил к воспроизведению файла \"%2$s\"", this.getName(), soundFile));
        
        int result = playSound(soundFile);
        
        logger.log(Level.INFO, String.format("%1$s завершил работу: %2$d", this.getName(), result));
        
        Runnable SendDataToApplication = () -> {
            if (result == 1)
                scene.logContent.addMessage(new LogMessage(new ContentElement(String.format("%1$s: имя файла не задано",
                                                                                            this.getName())),
                                                           LogMessage.MESSAGE_ERROR));
            
            else if (result == 2)
                scene.logContent.addMessage(new LogMessage(new ContentElement(String.format(
                        "%1$s: файл не найден: \"%2$s\"",
                        this.getName(),
                        soundFile)), LogMessage.MESSAGE_ERROR));
        };
        Platform.runLater(SendDataToApplication);
    }
    
    private int playSound(String soundFile)
    {
        if (soundFile == null || soundFile.isEmpty())
            return 1;
        
        File file = new File(soundFile);
        
        if (!file.exists() || file.isDirectory())
            return 2;
/*
        if(AudioSystem.getMixer(null).isOpen())
		{
			if (logger != null)
            logger.log(Level.WARNING, this.getName() + ": Mixer уже занят");
			return 3;
		}
*/
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file))
        {
            AudioListener listener = new AudioListener();
            DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.addLineListener(listener);
            clip.open(audioInputStream);
            try
            {
                clip.start();
                listener.waitUntilDone();
            }
            finally
            {
                clip.close();
            }
            return 0;
            
        }
        catch (LineUnavailableException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Ресурс не доступен.", this.getName()));
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Ошибка ввода-вывода.", this.getName()));
        }
        catch (IllegalArgumentException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Ошибка в аргументе.", this.getName()));
        }
        catch (IllegalStateException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Линия занята.", this.getName()));
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Недостаточно прав.", this.getName()));
        }
        catch (InterruptedException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Поток аварийно остановлен.", this.getName()));
        }
        catch (UnsupportedAudioFileException e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Формат не поддерживается.", soundFile));
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, String.format("%1$s: Ошибка", this.getName()), e);
        }
        
        return 4;
    }
}

class AudioListener implements LineListener
{
    private boolean done = false;
    
    @Override
    public synchronized void update(LineEvent event)
    {
        LineEvent.Type eventType = event.getType();
        if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE)
        {
            done = true;
            notifyAll();
        }
    }
    
    public synchronized void waitUntilDone() throws InterruptedException
    {
        while (!done)
        {
            wait();
        }
    }
}