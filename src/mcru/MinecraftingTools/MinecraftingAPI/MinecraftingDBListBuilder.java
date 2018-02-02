package mcru.MinecraftingTools.MinecraftingAPI;

import javafx.concurrent.Task;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfile;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.ApplicationControl.minecraftingProfiles;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Построение БД Игроков Майнкрафтинг в формате HTML
 */
public class MinecraftingDBListBuilder extends Task <String>
{
    private int countPlayers = 0;
    
    
    @Override
    protected String call() throws Exception
    {
        StringBuilder value = new StringBuilder();
        value.append(String.format("<HTML><HEAD><TITLE>База данных игроков Minecrafting (%1$s)</TITLE>\n\n" +
                                   "<META content=\"text/html\">\n<META charset=\"%2$s\"><STYLE>" +
                                   "A {word-wrap: break-word; font-size: %3$dpx; font-family: '%4$s', monospace; font-weight: bold; text-decoration: none; border-bottom: 1px dashed}" +
                                   "SPAN {font-size: %3$dpx; font-family: monospace; font-weight: bold; word-wrap: break-word;} " +
                                   "</STYLE></HEAD><BODY>",
                                   minecraftingProfiles.profiles.size(),
                                   config.Encoding,
                                   (int) config.CommonFontSize,
                                   config.WebViewFontFamily));
        
        for (MinecraftingProfile mp : minecraftingProfiles.profiles)
        {
            countPlayers++;
            message(mp.UUID);
            
            PlayerListElement ple = scene.getPlayerListElementByUUID(mp.UUID);
            if (ple != null)
            {
                value.append("<SPAN>Игрок:</SPAN>");
                value.append(String.format(
                        "<A href = \"#\" onclick = \"jsBridge.linkMouseClick('playerUUID', '%1$s');return false;\">" +
                        "%2$s\n</A><BR>",
                        mp.UUID,
                        mp.Nick));
            }
            else
                value.append(String.format("<SPAN>Игрок:%s\n</SPAN><BR>", mp.Nick));
            
            value.append(String.format("<SPAN>UUID: %s\n</SPAN><BR>", mp.UUID));
            value.append(String.format("<SPAN>Актуальность: %s\n</SPAN><BR>",
                                       TextFunc.DateTimeToString(mp.LongDateTime)));
            value.append("<SPAN>Санкции:\n</SPAN><BR>");
            if (mp.SanctionsList.isEmpty())
                value.append("<SPAN>-\n</SPAN><BR>");
            else
            {
                for (String s : mp.toStringSanctionList().split("\n"))
                {
                    value.append(String.format("<SPAN>%s\n</SPAN><BR>", s.replace(" ", "&nbsp;")));
                }
            }
            
            value.append("<SPAN>IP-адреса:\n</SPAN><BR>");
            if (mp.IPList.isEmpty())
                value.append("<SPAN>-\n</SPAN><BR>");
            else
            {
                for (String s : mp.toStringIPListFool().split("\n"))
                {
                    value.append(String.format("<SPAN>%s\n</SPAN><BR>", s.replace(" ", "&nbsp;")));
                }
            }
            
            value.append(String.format("<BR><BUTTON " +
                                       "onclick=\"jsBridge.linkMouseClick('MCRUDB-DeleteRecord', '%1$s');return false;\">" +
                                       "Удалить из БД</BUTTON>", mp.UUID));
            
            value.append("<SPAN>\n</SPAN>");
            value.append("<HR>");
            
            updateProgress(countPlayers, minecraftingProfiles.profiles.size());
        }
        value.append("<BR>");
        value.append(String.format("<SPAN>\nВсего записей:&nbsp;%d</SPAN>", countPlayers));
        value.append("<HR>");
        value.append("</BODY></HTML>");
        return value.toString();
    }
    
    private void message(String uuid) throws Exception
    {
        updateMessage(uuid);
    }
    
    public int getCountPlayers()
    {
        return countPlayers;
    }
}
