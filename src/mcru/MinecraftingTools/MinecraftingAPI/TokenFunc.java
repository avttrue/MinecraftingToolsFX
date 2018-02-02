package mcru.MinecraftingTools.MinecraftingAPI;

import mcru.MinecraftingTools.MyApplication;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Работа с токенами
 */
public class TokenFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * генерируем токен на базе UUID
     */
    public static String generateTokenByUUID(int length)
    {
        String token = UUID.randomUUID().toString().replace("-", "");
        
        if (length <= token.length())
            return token.substring(0, length);
        else
        {
            logger.log(Level.SEVERE,
                       String.format("Указан некорректный параметр length = %1$d, допустим максимальный = %2$d",
                                     length,
                                     token.length()));
            return null;
        }
    }
}
