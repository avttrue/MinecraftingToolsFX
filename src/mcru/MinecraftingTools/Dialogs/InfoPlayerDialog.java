package mcru.MinecraftingTools.Dialogs;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Border;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import mcru.MinecraftingTools.Functions.DesktopFunc;
import mcru.MinecraftingTools.Functions.ImgFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Helpers.PlayerListElement;
import mcru.MinecraftingTools.Interface.MyBorder;
import mcru.MinecraftingTools.MinecraftingAPI.Profile.MinecraftingProfile;
import mcru.MinecraftingTools.MojangAPI.MojangProfile;
import ontando.minecrafting.remote_access.env.DataValue;

import java.util.Map;

import static mcru.MinecraftingTools.ApplicationControl.*;
import static mcru.MinecraftingTools.Functions.TextFunc.*;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Окно информации о канале
 */
public class InfoPlayerDialog
{
    public InfoPlayerDialog(PlayerListElement ple)
    {
        Dialog dialog = new Dialog <>();
        dialog.initOwner(scene.getWindow());
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle(ple.toString());
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefSize(config.InfoWindowWidth, config.InfoWindowHeight);
        dialog.setGraphic(null);
        
        TabPane tabPane = new TabPane();
        TextArea taPlayerInfo = new TextArea();
        TextArea taPlayerStatus = new TextArea();
        TextArea taMinecrafting = new TextArea();
        TextFlow tfMojang = new TextFlow();
        
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        ButtonType copyButtonType = new ButtonType("");
        dialog.getDialogPane().getButtonTypes().addAll(copyButtonType, okButtonType);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        
        Button copyBytton = (Button) dialog.getDialogPane().lookupButton(copyButtonType);
        
        copyBytton.setGraphic(new ImageView(ResFunc.getImage("copy24")));
        copyBytton.setTooltip(new Tooltip("Копировать в буфер обмена"));
        copyBytton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            
            if (tabPane.getSelectionModel().isSelected(0))
                content.putString(taPlayerInfo.getText());
            else if (tabPane.getSelectionModel().isSelected(1))
                content.putString(taPlayerStatus.getText());
            else if (tabPane.getSelectionModel().isSelected(2))
                content.putString(taMinecrafting.getText());
            else if (tabPane.getSelectionModel().isSelected(3))
            {
                StringBuilder text = new StringBuilder();
                for (Node node : tfMojang.getChildren())
                {
                    if (node instanceof Text)
                        text.append(((Text) node).getText());
                }
                content.putString(text.toString());
            }
            clipboard.setContent(content);
        });
        
        // информация о канале
        taPlayerInfo.setWrapText(true);
        taPlayerInfo.setEditable(false);
        taPlayerInfo.setBorder(new Border(new MyBorder(5, 1)));
        
        // fields
        taPlayerInfo.setText("FIELDS\n");
        for (Map.Entry <String, DataValue> entry : ple.getFields().entrySet())
        {
            taPlayerInfo.appendText(entry.getValue() + "\n");
        }
        
        // properties
        long played_total = 0;
        long played_server = 0;
        taPlayerInfo.appendText("\nPROPERTIES\n");
        for (Map.Entry <String, DataValue> entry : ple.getProperties().entrySet())
        {
            taPlayerInfo.appendText(String.format("%s\n", entry.getValue()));
            
            if (entry.getKey().equals("played_total"))
                played_total = entry.getValue().asLong();
            if (entry.getKey().equals("played_server"))
                played_server = entry.getValue().asLong();
        }
        taPlayerInfo.positionCaret(0);
        
        // статус игрока
        taPlayerStatus.setWrapText(true);
        taPlayerStatus.setEditable(false);
        taPlayerStatus.setBorder(new Border(new MyBorder(5, 1)));
        
        taPlayerStatus.appendText(String.format("online_flags:\t%s\n", DecodePlayerOnlineFlags(ple.online_flags)));
        taPlayerStatus.appendText(String.format("status_flags:\t%s\n", DecodePlayerStatusFlags(ple.status_flags)));
        taPlayerStatus.appendText(String.format("played_total:\t%s\n", TimeIntervalToString(played_total, false)));
        taPlayerStatus.appendText(String.format("played_server:\t%s\n", TimeIntervalToString(played_server, false)));
        taPlayerStatus.positionCaret(0);
        
        // Minecrafting
        taMinecrafting.setWrapText(true);
        taMinecrafting.setEditable(false);
        taMinecrafting.setBorder(new Border(new MyBorder(5, 1)));
        
        MinecraftingProfile mp = minecraftingProfiles.find(ple.uuid);
        if (mp == null || (mp.IPList.isEmpty() && mp.SanctionsList.isEmpty()))
        {
            taMinecrafting.appendText("Информация отсутствует,\nотправьте запрос по игроку в Minectafting.ru");
        }
        else
        {
            taMinecrafting.appendText(String.format("Актуальность:\n%s\n", DateTimeToString(mp.LongDateTime)));
            
            if (!mp.SanctionsList.isEmpty())
                taMinecrafting.appendText(String.format("\nСанкции:\n%s\n", mp.toStringSanctionList()));
            else
                taMinecrafting.appendText("\nСанкции:\n - \n");
            
            
            if (!mp.IPList.isEmpty())
                taMinecrafting.appendText(String.format("\nIP-адреса:\n%s\n", mp.toStringIPListFool()));
            else
                taMinecrafting.appendText("\nIP-адреса:\n-\n");
        }
        taMinecrafting.positionCaret(0);
        
        // Mojang
        MojangProfile mProfile = mojangProfiles.find(ple.uuid);
        if (mProfile == null || mProfile.RequestDateTime.isEmpty())
        {
            tfMojang.getChildren().add(new Text("Информация отсутствует,\nотправьте запрос по игроку в Mojang API"));
        }
        else
        {
            Hyperlink hlNick = new Hyperlink("источник");
            hlNick.setFocusTraversable(false);
            hlNick.setOnAction(lambda -> {
                DesktopFunc.openWebResource(String.format(config.MojangApiNameHistory, mProfile.UUID.replace("-", "")),
                                            false);
                hlNick.setVisited(false);
            });
            
            tfMojang.getChildren().addAll(new Text(String.format("Актуальность:\n%s\n", mProfile.RequestDateTime)),
                                          new Text("\nИстория ника:\t"),
                                          hlNick,
                                          new Text(String.format("\n%s\n", mProfile.NameHistory)));
            
            
            if (!mProfile.SkinUrl.isEmpty())
            {
                tfMojang.getChildren().add(new Text("\nШкурка:\t"));
                Hyperlink hlSkin = new Hyperlink("источник");
                hlSkin.setFocusTraversable(false);
                hlSkin.setOnAction(lambda -> {
                    DesktopFunc.openWebResource(mProfile.SkinUrl, false);
                    hlSkin.setVisited(false);
                });
                tfMojang.getChildren().add(hlSkin);
                tfMojang.getChildren().add(new Text("\n\n"));
            }
            else
                tfMojang.getChildren().add(new Text("\nШкурка:\n - \n"));
            
            Image skin = ImgFunc.LoadImageFromBase64(mProfile.SkinImage, config.SkinViewerScale);
            if (skin == null)
                skin = ResFunc.getImage("stop24");
            tfMojang.getChildren().add(new ImageView(skin));
            
            if (!mProfile.CapeUrl.isEmpty())
            {
                tfMojang.getChildren().add(new Text("\nПлащ:\t"));
                Hyperlink hlCape = new Hyperlink("источник");
                hlCape.setFocusTraversable(false);
                hlCape.setOnAction(lambda -> {
                    DesktopFunc.openWebResource(mProfile.CapeUrl, false);
                    hlCape.setVisited(false);
                });
                tfMojang.getChildren().add(hlCape);
                tfMojang.getChildren().add(new Text("\n\n"));
            }
            else
                tfMojang.getChildren().add(new Text("\n\nПлащ:\n - \n"));
            
            Image cape = ImgFunc.LoadImageFromBase64(mProfile.CapeImage, config.SkinViewerScale);
            if (cape == null)
                cape = ResFunc.getImage("stop24");
            tfMojang.getChildren().add(new ImageView(cape));
            tfMojang.getChildren().add(new Text("\n\n"));
            Hyperlink hlWiki = new Hyperlink("http://wiki.vg/Mojang_API");
            hlWiki.setFocusTraversable(false);
            hlWiki.setOnAction(lambda -> {
                DesktopFunc.openWebResource(hlWiki.getText(), false);
                hlWiki.setVisited(false);
            });
            tfMojang.getChildren().add(hlWiki);
        }
        
        //////////////////////////////////////////////////////////////
        Tab tabInfo = new Tab("Информация", taPlayerInfo);
        tabInfo.setClosable(false);
        
        Tab tabStatus = new Tab("Статус", taPlayerStatus);
        tabStatus.setClosable(false);
        
        Tab tabMinecrafting = new Tab("Minecrafting.ru", taMinecrafting);
        tabMinecrafting.setClosable(false);
        
        ScrollPane spMojang = new ScrollPane(tfMojang);
        spMojang.setBorder(new Border(new MyBorder(5, 1)));
        Tab tabMojang = new Tab("Mojang API", spMojang);
        tabMojang.setClosable(false);
        
        tabPane.getTabs().addAll(tabInfo, tabStatus, tabMinecrafting, tabMojang);
        
        dialog.getDialogPane().setContent(tabPane);
        dialog.show();
    }
}
