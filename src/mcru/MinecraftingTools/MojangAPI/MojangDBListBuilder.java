package mcru.MinecraftingTools.MojangAPI;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import mcru.MinecraftingTools.Functions.ImgFunc;
import mcru.MinecraftingTools.Helpers.PlayerListElement;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.ApplicationControl.mojangProfiles;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Построение БД Игроков Можанг в формате HTML
 */
public class MojangDBListBuilder extends Task <String>
{
    private int countPlayers = 0;
    private int countCapes = 0;
    private int countSkins = 0;
    
    @Override
    protected String call() throws Exception
    {
        StringBuilder value = new StringBuilder();
        value.append(String.format("<!DOCTYPE html><HTML><HEAD><TITLE>База данных игроков Mojang (%1$s)</TITLE>\n\n" +
                                   "<META content=\"text/html\">\n<META charset=\"%2$s\"><STYLE>" +
                                   "A {word-wrap: break-word; font-size: %3$dpx; font-family: '%4$s', monospace; font-weight: bold; text-decoration: none; border-bottom: 1px dashed}" +
                                   "SPAN {font-size: %3$dpx; font-family: monospace; font-weight: bold; word-wrap: break-word;} " +
                                   "</STYLE></HEAD><BODY>",
                                   mojangProfiles.profiles.size(),
                                   config.Encoding,
                                   (int) config.CommonFontSize,
                                   config.WebViewFontFamily));
        
        for (MojangProfile mp : mojangProfiles.profiles)
        {
            countPlayers++;
            this.message(mp.UUID);
            
            PlayerListElement ple = scene.getPlayerListElementByUUID(mp.UUID);
            if (ple != null)
            {
                value.append(String.format(
                        "<A href = \"#\" onclick = \"jsBridge.linkMouseClick('playerUUID', '%1$s');return false;\">" +
                        "%1$s\n</A><BR>",
                        mp.UUID));
            }
            else
                value.append(String.format("<SPAN>%s\n</SPAN><BR>", mp.UUID));
            
            value.append(String.format("<SPAN>Актуальность: %s\n</SPAN><BR>", mp.RequestDateTime));
            
            value.append("<SPAN>История ника: </SPAN><BR>");
            
            for (String s : mp.NameHistory.split("\n"))
            {
                value.append(String.format("<SPAN>%s\n</SPAN><BR>", s));
            }
            
            value.append(String.format("<A HREF=\"%1$s\">Источник\n</A><BR>",
                                       String.format(config.MojangApiNameHistory, mp.UUID.replace("-", ""))));
            
            if (mp.SkinImage != null && !mp.SkinImage.isEmpty())
            {
                String b64 = mp.SkinImage;
                if (config.SkinViewerScale > 1) // корректное масштабирование
                {
                    Image skin = ImgFunc.LoadImageFromBase64(mp.SkinImage, config.SkinViewerScale);
                    b64 = ImgFunc.LoadBase64FromImage(skin, "png");
                }
                value.append(String.format("<BR><IMG SRC=\"data:imageKey/png;base64,%1$s\" " +
                                           "ALIGN=\"left\";><BR CLEAR = \"ALL\"\n><BR>", b64));
                value.append(String.format("<A HREF=\"%1$s\" TARGET=\"_blank\">Источник\n</A><BR>", mp.SkinUrl));
                countSkins++;
            }
            
            if (mp.CapeImage != null && !mp.CapeImage.isEmpty())
            {
                String b64 = mp.CapeImage;
                if (config.SkinViewerScale > 1) // корректное масштабирование
                {
                    Image cape = ImgFunc.LoadImageFromBase64(mp.CapeImage, config.SkinViewerScale);
                    b64 = ImgFunc.LoadBase64FromImage(cape, "png");
                }
                value.append(String.format("<BR><IMG SRC=\"data:imageKey/png;base64,%1$s\" " +
                                           "ALIGN=\"left\";><BR CLEAR = \"ALL\"\n><BR>", b64));
                value.append(String.format("<A HREF=\"%1$s\">Источник\n</A><BR>", mp.CapeUrl));
                countCapes++;
            }
            
            value.append(String.format("<BR><BUTTON " +
                                       "onclick=\"jsBridge.linkMouseClick('MojangDB-DeleteRecord', '%1$s');return false;\">" +
                                       "Удалить из БД</BUTTON>", mp.UUID));
            
            value.append("<SPAN>\n</SPAN>");
            value.append("<HR>");
            
            this.updateProgress(countPlayers, mojangProfiles.profiles.size());
        }
        value.append("<BR>");
        value.append(String.format("<SPAN>\nВсего записей:&nbsp;%d</SPAN><BR>", countPlayers));
        value.append(String.format("<SPAN>\nШкурок:&nbsp;%d</SPAN><BR>", countSkins));
        value.append(String.format("<SPAN>\nПлащей:&nbsp;%d</SPAN>", countCapes));
        value.append("<HR>");
        value.append("</BODY></HTML>");
        return value.toString();
    }
    
    private void message(String uuid) throws Exception
    {
        this.updateMessage(uuid);
    }
    
    public int getCountPlayers()
    {
        return countPlayers;
    }
}
