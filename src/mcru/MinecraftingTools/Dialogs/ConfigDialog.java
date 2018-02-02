package mcru.MinecraftingTools.Dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mcru.MinecraftingTools.Functions.FileFunc;
import mcru.MinecraftingTools.Functions.ResFunc;
import mcru.MinecraftingTools.Functions.SysFunc;
import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.LogMessage;
import mcru.MinecraftingTools.Interface.*;
import mcru.MinecraftingTools.MinecraftingAPI.Authentification.AuthentificationData;
import mcru.MinecraftingTools.Sound.SoundPlayer;

import java.io.File;

import static mcru.MinecraftingTools.ApplicationControl.MyWorkingDir;
import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.Helpers.StyleManager.checkAndLoadCSS;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Диалог настроек приложения
 */
public class ConfigDialog
{
    private CheckBox cbAlwaysOnTop = new CheckBox("Основное окно всегда сверху");
    private CheckBox cbPopUpMainWindowAtMessage = new CheckBox("Подниматься при получении новых сообщений");
    private CheckBox cbWriteMessagesToFile = new CheckBox("Писать все сообщения чата в файл");
    private CheckBox cbLeaveAllPartiesAtDisconnect = new CheckBox("Покидать все группы при выходе");
    private CheckBox cbShowIconsInChat =
            new CheckBox("Отображать в панели чата дополнительные иконки (предупреждения и пр.)");
    private CheckBox cbWriteContentSizeInTabs = new CheckBox("Отображать во вкладках размер документа");
    private CheckBox cbUseCSS = new CheckBox("Использовать дополнительный стиль отображения");
    private CheckBox cbShowDateMessage = new CheckBox("Отображать дату сообщения");
    private CheckBox cbShowSourceMessage = new CheckBox("Отображать источник сообщения");
    private CheckBox cbShowChannelMessage = new CheckBox("Отображать канал сообщения");
    private CheckBox cbShowAccessDialogAtStart = new CheckBox("Показывать диалог аутентификации при старте");
    private CheckBox cbShowMessagesFromMinecraftingTools = new CheckBox("Показывать сообщения от Minecrafting Tools");
    private CheckBox cbShowMessagesFromServerPlugin = new CheckBox("Показывать сообщения от серверного плагина");
    private CheckBox cbWriteMessageNumbers = new CheckBox("Отображать в сообщениях номера и отделять чертой");
    private CheckBox cbShowPlayerViolationMark = new CheckBox("Отображать на игроках отметку о наличии взысканий");
    private CheckBox cbRecognizeURLInMessage = new CheckBox("Распознавать веб-ссылки в чате");
    private CheckBox cbMouseWealZooming = new CheckBox("Использовать колесо мыши для масштабирования");
    private CheckBox cbToSaveTextsForWindowsOS =
            new CheckBox("Сохранять текстовые документы в формате Windows (\"\\n\" = \"\\r\\n\")");
    private CheckBox cbRepaintNodesAtNewMessage = new CheckBox("Обновлять содержание при получении новых сообщений");
    private CheckBox cbShiftEnterAsNewLine =
            new CheckBox("Использовать SHIFT+ENTER для перехода на новую строку в редакторе текста");
    private CheckBox cbTryToReconnected = new CheckBox("Пытаться заново подключиться к серверу при потере связи");
    private SelectFilePane sfpNewMessageSound;
    private SelectFilePane sfpPlayAlarmDisconnectSound;
    private IntegerSpinner spinnerContentTabCaptionLength;
    private IntegerSpinner spinnerChannelPlayerListLength;
    private IntegerSpinner spinnerSearchHistorySize;
    private IntegerSpinner spinnerPlayedServerAlertValue;
    private IntegerSpinner spinnerSkinViewerScale;
    private IntegerSpinner spinnerCommonFontWeight;
    private IntegerSpinner spinnerChatMaximumMessagesCount;
    private ColorPane cpThemeColor;
    private ColorPane cpNewMessageTabColor;
    private ColorPane cpCommonDataTimeColor;
    private ColorPane cpChatMessageSourceColor;
    private ColorPane cpChatMessageAuthorColor;
    private ColorPane cpChatMessageMessageColor;
    private ColorPane cpChatMessageChannelColor;
    private ColorPane cpEventErrorColor;
    private ColorPane cpEventInfoColor;
    private ColorPane cpEventGoodColor;
    private ColorPane cpEventOtherColor;
    private ComboboxPane cbpTimeZones;
    private ComboboxFontPane cbfpWebViewFontFamily;
    
    public ConfigDialog(Window parent)
    {
        Dialog <AuthentificationData> dialog = new Dialog <>();
        checkAndLoadCSS(dialog.getDialogPane());
        dialog.initOwner(parent);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(true);
        dialog.setTitle("Настройки");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setBorder(new Border(new MyBorder(6, 2)));
        dialog.getDialogPane().setPadding(new Insets(2, 2, 2, 2));
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(config.ConfigWindowWidth, config.ConfigWindowHeight);
        ButtonType okButtonType = new ButtonType("", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        ((Button) dialog.getDialogPane().lookupButton(okButtonType))
                .setGraphic(new ImageView(ResFunc.getImage("OK24")));
        ((Button) dialog.getDialogPane().lookupButton(okButtonType)).setText("");
        ((Button) dialog.getDialogPane().lookupButton(okButtonType)).setOnAction(lambda -> clickOK());
        
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setGraphic(new ImageView(ResFunc.getImage("cancel24")));
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction(lambda -> {
        });
        
        // ПОВЕДЕНИЕ
        cbAlwaysOnTop.setSelected(config.AlwaysOnTop);
        cbShowAccessDialogAtStart.setSelected(config.ShowAccessDialogAtStart);
        cbPopUpMainWindowAtMessage.setSelected(config.PopUpMainWindowAtMessage);
        cbWriteMessagesToFile.setSelected(config.WriteMessagesToFile);
        cbLeaveAllPartiesAtDisconnect.setSelected(config.LeaveAllPartiesAtDisconnect);
        cbWriteContentSizeInTabs.setSelected(config.WriteContentSizeInTabs);
        cbUseCSS.setSelected(config.UseCSS);
        cbShowPlayerViolationMark.setSelected(config.ShowPlayerViolationMark);
        cbRecognizeURLInMessage.setSelected(config.RecognizeURLInMessage);
        cbMouseWealZooming.setSelected(config.MouseWealZooming);
        cbToSaveTextsForWindowsOS.setSelected(config.ToSaveTextsForWindowsOS);
        cbRepaintNodesAtNewMessage.setSelected(config.RepaintNodesAtNewMessage);
        cbShiftEnterAsNewLine.setSelected(config.ShiftEnterAsNewLine);
        cbTryToReconnected.setSelected(config.TryToReconnected);
        
        spinnerContentTabCaptionLength =
                new IntegerSpinner("Допустимая длина вкладок:", null, 5, 100, config.ContentTabCaptionLength);
        spinnerChannelPlayerListLength = new IntegerSpinner("Допустимая длина записей в списках каналов/игроков:",
                                                            null,
                                                            5,
                                                            100,
                                                            config.ChannelPlayerListLength);
        spinnerSearchHistorySize =
                new IntegerSpinner("Допустимая объём истории поиска:", null, 1, 100, config.SearchHistorySize);
        spinnerPlayedServerAlertValue = new IntegerSpinner("Время в часах для тревоги на игроке:",
                                                           "(если меньше указанного, то игрок будет помечаться \"тревожным\")",
                                                           0,
                                                           9999,
                                                           config.PlayedServerAlertValue);
        
        spinnerSkinViewerScale = new IntegerSpinner("Масштаб увеличения изображения при просмотре шкурки:",
                                                    null,
                                                    1,
                                                    20,
                                                    config.SkinViewerScale);
        spinnerChatMaximumMessagesCount = new IntegerSpinner("Максимально отображаемое количество сообщений:",
                                                             null,
                                                             10,
                                                             999999,
                                                             config.ChatMaximumMessagesCount);
        
        sfpNewMessageSound = new SelectFilePane("Воспроизводить звук при поступлении сообщения",
                                                config.PlaySoundAtMessage,
                                                config.NotificationSoundFile,
                                                lambda -> openNewMessageSound(),
                                                lambda -> sfpNewMessageSound.setValue(config.NotificationSoundFile),
                                                lambda -> new SoundPlayer(sfpNewMessageSound.getValue()).start());
        
        sfpPlayAlarmDisconnectSound = new SelectFilePane("Воспроизводить звук при потере связи с сервером",
                                                         config.PlayAlarmDisconnectSound,
                                                         config.AlarmDisconnectSoundFile,
                                                         lambda -> openPlayAlarmDisconnectSound(),
                                                         lambda -> sfpPlayAlarmDisconnectSound
                                                                 .setValue(config.AlarmDisconnectSoundFile),
                                                         lambda -> new SoundPlayer(sfpPlayAlarmDisconnectSound
                                                                                           .getValue()).start());
        
        VBox vbBehavior = new VBox(cbAlwaysOnTop,
                                   cbShowAccessDialogAtStart,
                                   cbPopUpMainWindowAtMessage,
                                   cbWriteMessagesToFile,
                                   cbLeaveAllPartiesAtDisconnect,
                                   cbWriteContentSizeInTabs,
                                   cbShowPlayerViolationMark,
                                   cbUseCSS,
                                   cbRecognizeURLInMessage,
                                   cbMouseWealZooming,
                                   cbToSaveTextsForWindowsOS,
                                   cbRepaintNodesAtNewMessage,
                                   cbShiftEnterAsNewLine,
                                   cbTryToReconnected,
                                   sfpNewMessageSound,
                                   sfpPlayAlarmDisconnectSound,
                                   spinnerContentTabCaptionLength,
                                   spinnerChannelPlayerListLength,
                                   spinnerSearchHistorySize,
                                   spinnerPlayedServerAlertValue,
                                   spinnerSkinViewerScale,
                                   spinnerChatMaximumMessagesCount);
        vbBehavior.setSpacing(2);
        
        //оформление
        cpThemeColor =
                new ColorPane("Цветовая гамма оформления Minecrafting Tools:", null, Color.web(config.ThemeWebColor));
        cpNewMessageTabColor =
                new ColorPane("Цвет вкладок с новыми сообщениями:", null, Color.web(config.NewMessageTabWebColor));
        cpCommonDataTimeColor =
                new ColorPane("Цвет отображения даты и времени:", null, Color.web(config.CommonDataTimeWebColor));
        cpChatMessageSourceColor = new ColorPane("Цвет отображения источника сообщения:",
                                                 null,
                                                 Color.web(config.ChatMessageSourceWebColor));
        cpChatMessageAuthorColor =
                new ColorPane("Цвет отображения автора сообщения:", null, Color.web(config.ChatMessageAuthorWebColor));
        cpChatMessageChannelColor =
                new ColorPane("Цвет отображения канала сообщения:", null, Color.web(config.ChatMessageChannelWebColor));
        cpChatMessageMessageColor =
                new ColorPane("Цвет отображения текста сообщения:", null, Color.web(config.ChatMessageMessageWebColor));
        cpEventErrorColor =
                new ColorPane("Цвет отображения ошибки в событиях:", null, Color.web(config.EventErrorWebColor));
        cpEventInfoColor =
                new ColorPane("Цвет отображения информации в событиях:", null, Color.web(config.EventInfoWebColor));
        cpEventGoodColor =
                new ColorPane("Цвет отображения успеха в событиях:", null, Color.web(config.EventSeccessWebColor));
        cpEventOtherColor =
                new ColorPane("Цвет отображения сообщения в событиях:", null, Color.web(config.EventOtherWebColor));
        
        spinnerCommonFontWeight = new IntegerSpinner("Толщина начертания шрифта:",
                                                     "4 - нормальный, 7 - широкий",
                                                     1,
                                                     9,
                                                     config.CommonFontWeight);
        
        cbfpWebViewFontFamily =
                new ComboboxFontPane("Шрифт для отображения событий и чата:", null, config.WebViewFontFamily);
        
        VBox vbColors = new VBox(cpThemeColor,
                                 cpNewMessageTabColor,
                                 cpCommonDataTimeColor,
                                 cpChatMessageSourceColor,
                                 cpChatMessageAuthorColor,
                                 cpChatMessageMessageColor,
                                 cpChatMessageChannelColor,
                                 cpEventErrorColor,
                                 cpEventInfoColor,
                                 cpEventGoodColor,
                                 cpEventOtherColor,
                                 spinnerCommonFontWeight,
                                 cbfpWebViewFontFamily);
        vbColors.setSpacing(2);
        
        //ФОРМАТ
        cbpTimeZones = new ComboboxPane("Временная зона (рекомендуется указать зону сервера):",
                                        null,
                                        SysFunc.getTimeZoneList(),
                                        config.TimeZoneOffset);
        cbShowIconsInChat.setSelected(config.ShowIconsInChat);
        cbShowDateMessage.setSelected(config.ShowChatMessageDate);
        cbShowSourceMessage.setSelected(config.ShowChatMessageSource);
        cbShowChannelMessage.setSelected(config.ShowChatMessageChannel);
        cbWriteMessageNumbers.setSelected(config.WriteMessageNumbers);
        cbShowMessagesFromMinecraftingTools.setSelected(config.ShowMessagesFromMinecraftingTools);
        cbShowMessagesFromServerPlugin.setSelected(config.ShowMessagesFromServerPlugin);
        
        VBox vbFormat = new VBox(cbpTimeZones,
                                 cbShowIconsInChat,
                                 cbShowDateMessage,
                                 cbShowSourceMessage,
                                 cbShowChannelMessage,
                                 cbShowMessagesFromMinecraftingTools,
                                 cbShowMessagesFromServerPlugin,
                                 cbWriteMessageNumbers);
        vbFormat.setSpacing(2);
        
        TitledPane tpBehavior = new TitledPane("   Поведение", vbBehavior);
        tpBehavior.setPadding(new Insets(1, 0, 1, 0));
        tpBehavior.setExpanded(false);
        
        TitledPane tpColors = new TitledPane("   Цвета и оформление", vbColors);
        tpColors.setPadding(new Insets(1, 0, 1, 0));
        tpColors.setExpanded(false);
        
        TitledPane tpFormat = new TitledPane("   Формат данных", vbFormat);
        tpFormat.setPadding(new Insets(1, 0, 1, 0));
        tpFormat.setExpanded(false);
        
        ScrollPane spCommon = new ScrollPane(new VBox(tpBehavior, tpColors, tpFormat));
        spCommon.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spCommon.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        spCommon.setFitToWidth(true);
        
        //////////////////////////////////////////////
        Tab tabCommon = new Tab("Общие", spCommon);
        tabCommon.setClosable(false);
        
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tabCommon);
        
        dialog.getDialogPane().setContent(tabPane);
        dialog.showAndWait();
        
    }
    
    private void clickOK()
    {
        config.AlwaysOnTop = cbAlwaysOnTop.isSelected();
        config.ShowAccessDialogAtStart = cbShowAccessDialogAtStart.isSelected();
        config.PopUpMainWindowAtMessage = cbPopUpMainWindowAtMessage.isSelected();
        config.WriteMessagesToFile = cbWriteMessagesToFile.isSelected();
        config.LeaveAllPartiesAtDisconnect = cbLeaveAllPartiesAtDisconnect.isSelected();
        config.ShowIconsInChat = cbShowIconsInChat.isSelected();
        config.WriteContentSizeInTabs = cbWriteContentSizeInTabs.isSelected();
        config.UseCSS = cbUseCSS.isSelected();
        config.PlaySoundAtMessage = sfpNewMessageSound.getSelected();
        config.NotificationSoundFile = sfpNewMessageSound.getValue();
        config.PlayAlarmDisconnectSound = sfpPlayAlarmDisconnectSound.getSelected();
        config.AlarmDisconnectSoundFile = sfpPlayAlarmDisconnectSound.getValue();
        config.ContentTabCaptionLength = spinnerContentTabCaptionLength.getValue();
        config.ChannelPlayerListLength = spinnerChannelPlayerListLength.getValue();
        config.SearchHistorySize = spinnerSearchHistorySize.getValue();
        config.PlayedServerAlertValue = spinnerPlayedServerAlertValue.getValue();
        config.ThemeWebColor = TextFunc.ColorToRGBCode(cpThemeColor.getValue());
        config.NewMessageTabWebColor = TextFunc.ColorToRGBCode(cpNewMessageTabColor.getValue());
        config.CommonDataTimeWebColor = TextFunc.ColorToRGBCode(cpCommonDataTimeColor.getValue());
        config.ChatMessageSourceWebColor = TextFunc.ColorToRGBCode(cpChatMessageSourceColor.getValue());
        config.ChatMessageAuthorWebColor = TextFunc.ColorToRGBCode(cpChatMessageAuthorColor.getValue());
        config.ChatMessageMessageWebColor = TextFunc.ColorToRGBCode(cpChatMessageMessageColor.getValue());
        config.ChatMessageChannelWebColor = TextFunc.ColorToRGBCode(cpChatMessageChannelColor.getValue());
        config.EventErrorWebColor = TextFunc.ColorToRGBCode(cpEventErrorColor.getValue());
        config.EventInfoWebColor = TextFunc.ColorToRGBCode(cpEventInfoColor.getValue());
        config.EventSeccessWebColor = TextFunc.ColorToRGBCode(cpEventGoodColor.getValue());
        config.EventOtherWebColor = TextFunc.ColorToRGBCode(cpEventOtherColor.getValue());
        config.TimeZoneOffset = cbpTimeZones.getValue();
        config.ShowChatMessageDate = cbShowDateMessage.isSelected();
        config.ShowChatMessageSource = cbShowSourceMessage.isSelected();
        config.ShowChatMessageChannel = cbShowChannelMessage.isSelected();
        config.ShowMessagesFromMinecraftingTools = cbShowMessagesFromMinecraftingTools.isSelected();
        config.ShowMessagesFromServerPlugin = cbShowMessagesFromServerPlugin.isSelected();
        config.WriteMessageNumbers = cbWriteMessageNumbers.isSelected();
        config.SkinViewerScale = spinnerSkinViewerScale.getValue();
        config.ShowPlayerViolationMark = cbShowPlayerViolationMark.isSelected();
        config.CommonFontWeight = spinnerCommonFontWeight.getValue();
        config.ChatMaximumMessagesCount = spinnerChatMaximumMessagesCount.getValue();
        config.RecognizeURLInMessage = cbRecognizeURLInMessage.isSelected();
        config.MouseWealZooming = cbMouseWealZooming.isSelected();
        config.ToSaveTextsForWindowsOS = cbToSaveTextsForWindowsOS.isSelected();
        config.RepaintNodesAtNewMessage = cbRepaintNodesAtNewMessage.isSelected();
        config.ShiftEnterAsNewLine = cbShiftEnterAsNewLine.isSelected();
        config.TryToReconnected = cbTryToReconnected.isSelected();
        config.WebViewFontFamily = cbfpWebViewFontFamily.getValue();
        
        // дополнительные действия
        checkAndLoadCSS();
        scene.updatePLE();
        scene.updateCLE();
        
        if (config.ShiftEnterAsNewLine)
            scene.buttonSendMessage.setTooltip(new Tooltip("Отправить сообщение\n[Enter]"));
        else
            scene.buttonSendMessage.setTooltip(new Tooltip("Отправить сообщение\n[Shift+Enter]"));
        
        Platform.runLater(() -> scene.logContent.addMessage(new LogMessage(
                "Настройки приняты. Если вы меняли правила отображения чата или " +
                "панели событий, воспользуйтесь командой \"Перезагрузить документ\"" + " в контекстном меню",
                LogMessage.MESSAGE_INFO)));
    }
    
    // sfpNewMessageSound: NotificationSoundFile, PlaySoundAtMessage
    private void openNewMessageSound()
    {
        String fname = FileFunc.showOpenFileDialog(scene.getWindow(),
                                                   MyWorkingDir + File.separator + "Sounds",
                                                   new FileChooser.ExtensionFilter("WAV файлы (*.wav)", "*.wav"),
                                                   new FileChooser.ExtensionFilter("звуковые файлы (*.*)", "*.*"));
        if (fname.isEmpty())
            return;
        
        sfpNewMessageSound.setValue(fname);
    }
    
    // sfpPlayAlarmDisconnectSound: NotificationSoundFile, PlaySoundAtMessage
    private void openPlayAlarmDisconnectSound()
    {
        String fname = FileFunc.showOpenFileDialog(scene.getWindow(),
                                                   MyWorkingDir + File.separator + "Sounds",
                                                   new FileChooser.ExtensionFilter("WAV файлы (*.wav)", "*.wav"),
                                                   new FileChooser.ExtensionFilter("звуковые файлы (*.*)", "*.*"));
        if (fname.isEmpty())
            return;
        
        sfpPlayAlarmDisconnectSound.setValue(fname);
    }
}
