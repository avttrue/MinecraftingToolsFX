package mcru.MinecraftingTools.Helpers;

/**
 * Для фильтрации сообщений по табам в форме основного окна приложения в {@link mcru.MinecraftingTools.MainScene#tabpaneContent}
 * <br>
 */
public class ContentFilter
{
    public PlayerListElement player;
    public ChannelListElement channel;
    
    public ContentFilter(ChannelListElement channel, PlayerListElement player)
    {
        this.channel = channel;
        this.player = player;
    }
}

