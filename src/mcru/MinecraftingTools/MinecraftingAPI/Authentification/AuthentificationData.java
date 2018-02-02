package mcru.MinecraftingTools.MinecraftingAPI.Authentification;

import mcru.MinecraftingTools.Functions.CryptoFunc;

import java.util.Date;

/**
 * Aутентификационные данные<br>
 * @see AuthentificationList
 */
public class AuthentificationData
{
    /**
     * Имя сервера в виде [адрес]:[порт]
     */
    private String ServerName = "";
    /**
     * Локальный токен
     */
    private String LocalToken = "";
    /**
     * Имя пользователя
     */
    private String UserName = "";
    /**
     * Данные по последней удачной уатентификации на сервере {@link ServerAuthInformation}
     */
    private ServerAuthInformation ServerAuthInformation;
    /**
     * Пароль в зашифрованном виде
     */
    private String Password = "";
    /**
     * время последнего изменения
     */
    private long LastUpdate;
    
    public AuthentificationData()
    {
        LastUpdate = new Date().getTime();
    }
    
    public AuthentificationData(AuthentificationData authData)
    {
        LastUpdate = new Date().getTime();
        this.ServerName = authData.ServerName;
        this.LocalToken = authData.LocalToken;
        this.UserName = authData.UserName;
        this.Password = authData.Password;
        this.ServerAuthInformation = new ServerAuthInformation(authData.ServerAuthInformation.UserToken,
                                                               authData.ServerAuthInformation.Session);
        
    }
    
    /**
     * Получить пароль в расшифрованном виде
     */
    public String getPassword()
    {
        if (this.Password != null && !this.Password.isEmpty())
            return CryptoFunc.decrypt(this.Password);
        else
            return "";
    }
    
    /**
     * Установить пароль в зашифрованном виде
     * @param password пароль в расшифрованном виде
     */
    public final void setPassword(String password)
    {
        if (password != null && !password.isEmpty())
            this.Password = CryptoFunc.encrypt(password);
        else
            this.Password = "";
        
        LastUpdate = new Date().getTime();
    }
    
    public long getLastUpdate()
    {
        return LastUpdate;
    }
    
    public ServerAuthInformation getServerAuthInformation()
    {
        return ServerAuthInformation;
    }
    
    public void setServerAuthInformation(ServerAuthInformation serverAuthInformation)
    {
        this.ServerAuthInformation = serverAuthInformation;
        LastUpdate = new Date().getTime();
    }
    
    public String getLocalToken()
    {
        return LocalToken;
    }
    
    public void setLocalToken(String localToken)
    {
        LocalToken = localToken;
        LastUpdate = new Date().getTime();
    }
    
    public String getServerName()
    {
        return ServerName;
    }
    
    public void setServerName(String serverName)
    {
        ServerName = serverName;
        LastUpdate = new Date().getTime();
    }
    
    public String getUserName()
    {
        return UserName;
    }
    
    public void setUserName(String userName)
    {
        UserName = userName;
        LastUpdate = new Date().getTime();
    }
    
    @Override
    public final String toString()
    {
        return this.ServerName;
    }
}
