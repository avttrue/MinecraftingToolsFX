package mcru.MinecraftingTools.MinecraftingAPI.Profile;

import mcru.MinecraftingTools.Functions.TextFunc;
import mcru.MinecraftingTools.Helpers.PlayerListElement;

import static mcru.MinecraftingTools.MyApplication.scene;

/**
 * Класс для хранения санкции для профиля игрока {@link MinecraftingProfile}
 */
public class Sanction implements Cloneable
{
    private long date;
    private long cancel_date;
    private long expiration_date;
    private int moderator;
    private String reason;
    private String cancel_reason;
    private String sanction;
    
    public Sanction(
            long date, long cancel_date, long expiration_date, int moderator, String reason, String cancel_reason,
            String sanction)
    {
        this.date = date;
        this.cancel_date = cancel_date;
        this.expiration_date = expiration_date;
        this.moderator = moderator;
        this.reason = reason;
        this.cancel_reason = cancel_reason;
        this.sanction = sanction;
    }
    
    public int getModerator()
    {
        return moderator;
    }
    
    public long getCancel_date()
    {
        return cancel_date;
    }
    
    public long getDate()
    {
        return date;
    }
    
    public long getExpiration_date()
    {
        return expiration_date;
    }
    
    public String getCancel_reason()
    {
        return cancel_reason;
    }
    
    public String getReason()
    {
        return reason;
    }
    
    public String getSanction()
    {
        return sanction;
    }
    
    /**
     * преобразовать в String
     */
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        if (date > 0)
            result.append(String.format("Осужден:     %1$s %2$s\n",
                                        TextFunc.GetServerMessageDate(date),
                                        TextFunc.GetServerMessageTime(date)));
        
        
        if (cancel_date > 0)
            result.append(String.format("Свободен с:  %1$s %2$s\n",
                                        TextFunc.GetServerMessageDate(cancel_date),
                                        TextFunc.GetServerMessageTime(cancel_date)));
        
        
        if (expiration_date > 0)
            result.append(String.format("Освободится: %1$s %2$s\n",
                                        TextFunc.GetServerMessageDate(expiration_date),
                                        TextFunc.GetServerMessageTime(expiration_date)));
        
        
        if (!reason.isEmpty())
            result.append(String.format("Вина:        %1$s\n", reason));
        
        if (!sanction.isEmpty())
            result.append(String.format("Кара:        %1$s\n", sanction));
        
        if (!cancel_reason.isEmpty())
            result.append(String.format("Освобождён:  %1$s\n", cancel_reason));
        
        if (moderator > -1)
        {
            PlayerListElement ple = scene.getPlayerListElementByID(moderator);
            result.append(ple != null ? String.format("Модератор:   %1$s", ple.nick) : String
                    .format("Модератор:   не найден (ID = %1$d)\nповторите запрос по игроку", moderator));
        }
        else
            result.append("Модератор:   ошибка (ID = -1)");
        
        
        return result.toString();
    }
    
    /**
     * клонировать
     */
    @Override
    public Sanction clone()
    {
        return new Sanction(this.date,
                            this.cancel_date,
                            this.expiration_date,
                            this.moderator,
                            this.reason,
                            this.cancel_reason,
                            this.sanction);
    }
}