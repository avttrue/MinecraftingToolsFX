package mcru.MinecraftingTools.Helpers;

import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.MinecraftingAPI.MC_Message;
import mcru.MinecraftingTools.MyApplication;
import mcru.MinecraftingTools.Sound.SoundPlayer;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static mcru.MinecraftingTools.ApplicationControl.config;
import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Сообщение для размещения в чате. Отправка происходит в
 * {@link mcru.MinecraftingTools.MainScene#addMessageToChat(MC_Message)}
 */
public class ChatMessage
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    private ArrayList <ContentElement> message = new ArrayList <>();
    private String msgColor = null;
    private String msgClass = null;
    
    public ChatMessage(MC_Message mc_message)
    {
        // иконка с информацие о сообщении
        if (config.ShowIconsInChat)
        {
            switch (mc_message.type)
            {
                case MC_Message.SIMPLE_MESSAGE:
                    msgClass = "bubble16";
                    break;
                case MC_Message.INFORMATION:
                    msgClass = "info16";
                    break;
                case MC_Message.INCOMING:
                    msgClass = "next16";
                    break;
                case MC_Message.OUTCOMING:
                    msgClass = "prev16";
                    break;
                case MC_Message.FROM_PLUGIN:
                    msgClass = "service16";
                    break;
                case MC_Message.CHAT_MESSAGE:
                    msgClass = "chat16";
                    break;
                default:
                    msgClass = "bubble16";
            }
        }
        
        // определяем текст канала для корректной подсветки
        ChannelListElement cle = scene.getChannelListElementByID(mc_message.channel_id);
        String channelName = "";
        if (mc_message.channel_id > -1)
        {
            if (cle != null)
                channelName = cle.name;
        }
        else if (!mc_message.source.equals(config.SourceIsMinecraftingTools))
            channelName = config.ChannelIsPersonalMessage; // ЛС
        
        // подсветка сообщений
        for (HighlightElement hle : config.MessageHighlightsList)
        {
            if (hle.isOn && TextFunc.applySimpleRegExp(mc_message.message, hle.textRegexp))
            {
                logger.log(Level.INFO, String.format("Сработал регексп по сообщению \"%1$s\"", hle.caption));
                // цвет
                msgColor = hle.colorBackground;
                // звук
                if (hle.isPlaySound)
                    new SoundPlayer(hle.soundPath).start();
                
                break;
            }
        }
        
        // подсветка канала
        if (!channelName.isEmpty() && msgColor == null)
        {
            // смогли опредилить канал
            // не сработали предыдущие подсветки
            for (HighlightElement hle : config.ChannelHighlightsList)
            {
                if (hle.isOn && TextFunc.applySimpleRegExp(channelName, hle.textRegexp))
                {
                    logger.log(Level.INFO, String.format("Сработал регексп по каналу \"%1$s\"", hle.caption));
                    // цвет
                    msgColor = hle.colorBackground;
                    // звук
                    if (hle.isPlaySound)
                        new SoundPlayer(hle.soundPath).start();
                    
                    break;
                }
            }
        }
        
        // подсветка источника
        if (mc_message.source != null && !mc_message.source.isEmpty() && msgColor == null)
        {
            // не сработали предыдущие подсветки
            for (HighlightElement hle : config.SourceHighlightsList)
            {
                if (hle.isOn && TextFunc.applySimpleRegExp(mc_message.source, hle.textRegexp))
                {
                    logger.log(Level.INFO, String.format("Сработал регексп по источнику \"%1$s\"", hle.caption));
                    // цвет
                    msgColor = hle.colorBackground;
                    // звук
                    if (hle.isPlaySound)
                        new SoundPlayer(hle.soundPath).start();
                    
                    break;
                }
            }
        }
        
        // дата или дата-время
        if (!mc_message.time.isEmpty())
        {
            ContentElement ce;
            
            if (config.ShowChatMessageDate)
                ce = new ContentElement(String.format("%1$s%2$s %3$s%4$s ",
                                                      config.OpeningBraceForMessageDateTime,
                                                      mc_message.date,
                                                      mc_message.time,
                                                      config.ClosingBraceForMessageDateTime));
            else
                ce = new ContentElement(String.format("%1$s%2$s%3$s ",
                                                      config.OpeningBraceForMessageDateTime,
                                                      mc_message.time,
                                                      config.ClosingBraceForMessageDateTime));
            
            ce.addElementClass("date-time");
            message.add(ce);
        }
        
        // источник сообщения
        if (config.ShowChatMessageSource && mc_message.source != null && !mc_message.source.isEmpty())
        {
            message.add(new ContentElement(String.format("%1$s%2$s%3$s ",
                                                         config.OpeningBraceForMessageSource,
                                                         mc_message.source,
                                                         config.ClosingBraceForMessageSource))
                                .addElementClass("source"));
            
        }
        // Канал
        if (config.ShowChatMessageChannel && !channelName.isEmpty())
        {
            if (mc_message.channel_id > -1)
            {
                if (cle != null)
                {
                    message.add(new ContentElement(String.format("%1$s%2$s%3$s",
                                                                 config.OpeningBraceForMessageChannel,
                                                                 channelName,
                                                                 config.ClosingBraceForMessageChannel),
                                                   new MyLink("channelID", String.valueOf(cle.id)).get())
                                        .addElementClass("channel"));
                    // пробел между каналом и игроком
                    message.add(new ContentElement(" "));
                }
                else
                {
                    if (mc_message.channel_id > -1)
                        logger.log(Level.SEVERE,
                                   String.format("Канал из сообщения не найден: %1$d", mc_message.channel_id));
                }
            }
            else
            {
                message.add(new ContentElement(String.format("%1$s%2$s%3$s ",
                                                             config.OpeningBraceForMessageChannel,
                                                             channelName,
                                                             config.ClosingBraceForMessageChannel))
                                    .addElementClass("channel"));
            }
        }
        
        // вывод игрока и оппонента
        // если автор совпадает с оппонентом, значит пишут нам
        if (mc_message.author_id > -1 && mc_message.opponent_id > -1 && mc_message.author_id == mc_message.opponent_id)
        {
            PlayerListElement ple = scene.getPlayerListElementByID(mc_message.author_id);
            if (ple != null)
            {
                ContentElement ce = new ContentElement(String.format("%1$s ", config.PMessageIncomingSymbol));
                
                
                if (config.ShowIconsInChat && ple.isAlert())
                    ce.addElementClass("image-left").addElementClass("red_bookmark16");
                
                message.add(ce.addElementClass("player"));
                
                message.add(new ContentElement(String.format("%1$s%2$s%3$s",
                                                             config.OpeningBraceForMessageAuthor,
                                                             ple.nick,
                                                             config.ClosingBraceForMessageAuthor),
                                               new MyLink("playerID", String.valueOf(ple.id)).get())
                                    .addElementClass("player"));
            }
            else
            {
                if (mc_message.author_id > -1)
                    logger.log(Level.SEVERE,
                               String.format("author_id из сообщения не найден: %1$d", mc_message.author_id));
            }
        }
        else //если автор несовпадает с оппонентом
        {
            // определяем автора
            if (mc_message.author_id > -1)
            {
                PlayerListElement ple = scene.getPlayerListElementByID(mc_message.author_id);
                if (ple != null)
                {
                    
                    ContentElement ce = new ContentElement(String.format("%1$s%2$s%3$s",
                                                                         config.OpeningBraceForMessageAuthor,
                                                                         ple.nick,
                                                                         config.ClosingBraceForMessageAuthor),
                                                           new MyLink("playerID", String.valueOf(ple.id)).get());
                    
                    if (config.ShowIconsInChat && ple.isAlert())
                        ce.addElementClass("image-left").addElementClass("red_bookmark16");
                    
                    message.add(ce.addElementClass("player"));
                    
                }
                else
                {
                    if (mc_message.author_id > -1)
                        logger.log(Level.SEVERE,
                                   String.format("author_id из сообщения не найден: %1$d", mc_message.author_id));
                }
            }
            
            //пытаемся подставить автора по associated_user если автор не указан
            else if (mc_message.associated_user > -1)
            {
                PlayerListElement ple = scene.getPlayerListElementByID(mc_message.associated_user);
                if (ple != null)
                {
                    ContentElement ce = new ContentElement(String.format("%1$s%2$s%3$s",
                                                                         config.OpeningBraceForMessageAssPlayer,
                                                                         ple.nick,
                                                                         config.ClosingBraceForMessageAssPlayer),
                                                           new MyLink("playerID", String.valueOf(ple.id)).get());
                    
                    if (config.ShowIconsInChat && ple.isAlert())
                        ce.addElementClass("image-left").addElementClass("red_bookmark16");
                    
                    message.add(ce.addElementClass("player"));
                }
                else
                {
                    if (mc_message.associated_user > -1)
                        logger.log(Level.SEVERE,
                                   String.format("associated_user из сообщения не найден: %1$d",
                                                 mc_message.associated_user));
                }
            }
            
            //если есть оппонент
            PlayerListElement ple = scene.getPlayerListElementByID(mc_message.opponent_id);
            if (ple != null)
            {
                ContentElement ce = new ContentElement(String.format(" %1$s ", config.PMessageAuthorOpponentSeparator));
                
                if (config.ShowIconsInChat && ple.isAlert())
                    ce.addElementClass("image-left").addElementClass("red_bookmark16");
                
                message.add(ce.addElementClass("player"));
                
                message.add(new ContentElement(String.format("%1$s%2$s%3$s",
                                                             config.OpeningBraceForMessageAuthor,
                                                             ple.nick,
                                                             config.ClosingBraceForMessageAuthor),
                                               new MyLink("playerID", String.valueOf(ple.id)).get())
                                    .addElementClass("player"));
            }
            else
            {
                if (mc_message.opponent_id > -1)
                    logger.log(Level.SEVERE,
                               String.format("opponent_id из сообщения не найден: %1$d", mc_message.opponent_id));
            }
        }
        
        // текст сообщения
        message.add(new ContentElement(String.format(" %1$s\n", mc_message.message)).addElementClass("message"));
    }
    
    /**
     * Получить сообщение
     */
    public ArrayList <ContentElement> get()
    {
        return message;
    }
    
    /**
     * Добавить элемент
     */
    public ArrayList <ContentElement> add(ContentElement element)
    {
        message.add(element);
        return message;
    }
    
    @Override
    public String toString()
    {
        StringBuilder text = new StringBuilder();
        for (ContentElement ce : message)
        {
            if (ce.text != null)
                text.append(ce.text);
        }
        
        return text.toString();
    }
    
    public String getMsgClass()
    {
        return msgClass;
    }
    
    public String getMsgColor()
    {
        return msgColor;
    }
}
