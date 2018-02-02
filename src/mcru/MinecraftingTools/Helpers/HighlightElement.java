package mcru.MinecraftingTools.Helpers;

/**
 * элемента списка подсветки каналов и сообщений
 */
public class HighlightElement
{
    public String caption = "Highlight";
    public String textRegexp = "";
    public String soundPath = "";
    public boolean isPlaySound = false;
    public boolean isOn = false;
    public String colorBackground = "FFFD4F";
    
    public HighlightElement()
    {
    }
    
    public HighlightElement(String caption, String textRegexp, boolean isOn, String colorBackground)
    {
        this.caption = caption;
        this.textRegexp = textRegexp;
        this.isOn = isOn;
        this.colorBackground = colorBackground;
    }
    
    @Override
    public String toString()
    {
        return caption.isEmpty() ? "<Highlight>" : caption;
    }
}
