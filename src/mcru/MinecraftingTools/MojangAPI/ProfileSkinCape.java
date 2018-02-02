package mcru.MinecraftingTools.MojangAPI;

/**
 * используется для извлечения данных о профиле игрока с сайта можангов {@link MojangProfileWebSearcher}
 * это элемент данных истории профиля
 */
public class ProfileSkinCape
{
    public String id = "";
    public String name = "";
    public SkinCapeProperty[] properties = null;
}

class SkinCapeProperty
{
    public String name = "";
    /**
     * это поле содержит класс {@link SkinCapePropertyValue} в формате base64
     */
    public String value = null;
    public String signature = null;
}