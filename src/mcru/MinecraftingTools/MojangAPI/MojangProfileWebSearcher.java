package mcru.MinecraftingTools.MojangAPI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.image.Image;
import mcru.MinecraftingTools.ApplicationControl;
import mcru.MinecraftingTools.Functions.ImgFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.ContentElement;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Helpers.MyLink;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static mcru.MinecraftingTools.ApplicationControl.mojangProfiles;
import static mcru.MinecraftingTools.ApplicationControl.setStatusMojangProfileWebSearcher;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * получаем данные об игроке <br>
 * см.: http://wiki.vg/Mojang_API
 * @see MojangProfiles
 */
public class MojangProfileWebSearcher extends Thread
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    private long player_id;
    private String uuid;
    private String nameHistory = "нет данных";
    private String SkinUrl = "";
    private Image Skin = null;
    private String CapeUrl = "";
    private Image Cape = null;
    private String NameHistoryWebAddress = ApplicationControl.config.MojangApiNameHistory;
    private String ProfileSkinCapeWebAddress = ApplicationControl.config.MojangApiProfile;
    
    public MojangProfileWebSearcher(String uuid, long player_id)
    {
        this.uuid = uuid;
        this.player_id = player_id;
    }
    
    public final void run()
    {
        this.setName(String.format("MojangProfileWebSearcher (%1$d)", this.getId()));
        logger.log(Level.INFO, String.format("%1$s начал работу", this.getName()));
        logger.log(Level.INFO, String.format("Ищем профиль по UUID: %1$s", uuid));
        
        ReadNameHistoryFromWeb();
        ReadProfileSkinCapeFromWeb();
        
        Runnable SendDataToApplication = () -> {
            PlayerListElement ple = scene.getPlayerListElementByID(player_id);
            if (ple == null)
            {
                logger.log(Level.SEVERE, String.format("Игрок с ID=%1$d не найден", player_id));
                return;
            }
            
            MojangProfile mp = new MojangProfile();
            mp.UUID = ple.uuid;
            mp.NameHistory = nameHistory.replace(",", ",\n");
            mp.CapeUrl = CapeUrl;
            mp.SkinUrl = SkinUrl;
            mp.CapeImage = ImgFunc.LoadBase64FromImage(Cape, "png");
            mp.SkinImage = ImgFunc.LoadBase64FromImage(Skin, "png");
            mp.RequestDateTime = TextFunc.DateTimeToString(new Date().getTime());
            
            // удаляю прежнюю запись, если была
            mojangProfiles.remove(mp.UUID);
            // добавляю
            mojangProfiles.add(mp);
            logger.log(Level.INFO, String.format("Объём базы игроков: %1$d", ApplicationControl.mojangProfiles.size()));
            
            ArrayList <ContentElement> message = new ArrayList <>();
            message.add(new ContentElement("Получены данные об игроке "));
            message.add(new ContentElement(ple.nick, new MyLink("playerID", String.valueOf(player_id)).get())
                                .addElementClass("success-message"));
            message.add(new ContentElement(String.format(" (%1$s) от Mojang API", ple.uuid)));
            Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(message, LogMessage.MESSAGE_SUCCESS)));
            scene.updatePLE();
            setStatusMojangProfileWebSearcher(false);
        };
        Platform.runLater(SendDataToApplication);
        logger.log(Level.INFO, String.format("%1$s закончил работу", this.getName()));
    }
    
    /**
     * лезем на сайт и читаем данные профиля - история имени
     */
    private void ReadNameHistoryFromWeb()
    {
        if (uuid == null || uuid.isEmpty())
        {
            return;
        }
        
        String url = String.format(NameHistoryWebAddress, uuid);
        
        try (InputStream inputstream = new URL(url).openStream())
        {
            logger.log(Level.INFO, String.format("Ищем профиль по адресу: %1$s", url));
            
            nameHistory =
                    new BufferedReader(new InputStreamReader(inputstream)).lines().collect(Collectors.joining("\n"));
            
            logger.log(Level.INFO, String.format("Нашли: %1$s", nameHistory));
            
            // преобразуем в читаемый вид
            Gson gson = new Gson();
            List <NickHistoryElement> list =
                    gson.fromJson(nameHistory, new TypeToken <List <NickHistoryElement>>() {}.getType());
            for (NickHistoryElement ame : list)
            {
                if (ame.changedToAt > 0)
                {
                    nameHistory = nameHistory
                            .replace(String.valueOf(ame.changedToAt), TextFunc.DateTimeToString(ame.changedToAt));
                }
            }
        }
        catch (IOException e)
        {
            logger.log(Level.INFO, "Ошибка при чтении данных профиля", e);
        }
    }
    
    /**
     * лезем на сайт и читаем данные профиля - данные профиля
     */
    private void ReadProfileSkinCapeFromWeb()
    {
        if (uuid == null || uuid.isEmpty())
        {
            return;
        }
        
        String url = String.format(ProfileSkinCapeWebAddress, uuid);
        
        try (InputStream inputstream = new URL(url).openStream())
        {
            logger.log(Level.INFO, String.format("Ищем дополнительные данные профиля по адресу: %1$s", url));
            
            String profileSkinCape =
                    new BufferedReader(new InputStreamReader(inputstream)).lines().collect(Collectors.joining("\n"));
            
            try
            {
                Gson gsonProfileSkinCape = new Gson();
                Type typeProfileSkinCape = new TypeToken <ProfileSkinCape>()
                {}.getType();
                ProfileSkinCape sc = gsonProfileSkinCape.fromJson(profileSkinCape, typeProfileSkinCape);
                
                if (sc != null && sc.properties != null && sc.properties.length > 0)
                {
                    String value = "";
                    for (SkinCapeProperty p : sc.properties)
                    {
                        if (p.value != null)
                            value = p.value;
                    }
                    byte[] decoded = Base64.getDecoder().decode(value);
                    value = new String(decoded, "UTF-8");
                    
                    Gson gsonSkinCapeProperty = new Gson();
                    SkinCapePropertyValue scpv =
                            gsonSkinCapeProperty.fromJson(value, new TypeToken <SkinCapePropertyValue>() {}.getType());
                    
                    if (scpv.textures.SKIN != null && scpv.textures.SKIN.url != null &&
                        !scpv.textures.SKIN.url.isEmpty())
                    {
                        SkinUrl = scpv.textures.SKIN.url;
                        Skin = ImgFunc.LoadImageFromUrl(SkinUrl, false);
                    }
                    
                    if (scpv.textures.CAPE != null && scpv.textures.CAPE.url != null &&
                        !scpv.textures.CAPE.url.isEmpty())
                    {
                        CapeUrl = scpv.textures.CAPE.url;
                        Cape = ImgFunc.LoadImageFromUrl(CapeUrl, false);
                    }
                }
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Дополнительные данные профиля извлечь не удалось", e);
            }
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Дополнительные данные профиля извлечь не удалось, ошибка ввода-вывода", e);
        }
    }
}