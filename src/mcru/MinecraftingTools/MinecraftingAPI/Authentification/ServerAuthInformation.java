package mcru.MinecraftingTools.MinecraftingAPI.Authentification;

/**
 * Данные по последней удачной уатентификации на сервере
 */
public class ServerAuthInformation
{
    public byte[] UserToken;
    public String Session;
    
    public ServerAuthInformation(byte[] userToken, String session)
    {
        this.Session = session;
        this.UserToken = userToken;
    }
}
