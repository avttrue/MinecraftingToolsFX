package mcru.MinecraftingTools.MojangAPI;

/**
 * используется для извлечения данных о профиле игрока с сайта можангов (MojangProfileWebSearcher)
 * это элемент данных истории профиля
 * @see MojangProfileWebSearcher
 */
public class SkinCapePropertyValue
{
    public long timestamp = -1;
    public String profileId = "";
    public String profileName = "";
    public boolean isPublic = false;
    public Textures textures = null;
}

class Textures
{
    public ResUrl SKIN = null;
    public ResUrl CAPE = null;
}

class ResUrl
{
    public String url = "";
}