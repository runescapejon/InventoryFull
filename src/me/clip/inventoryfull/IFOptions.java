package me.clip.inventoryfull;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class IFOptions
{
  private int cooldownTime;
  private int maxAlerts;
  private boolean useHolo;
  private List<String> holoMsg;
  private int holoTime;
  private boolean useTitleManager;
  private String titleMsg;
  private String subTitleMsg;
  private int fadeIn;
  private int fadeOut;
  private int duration;
  private boolean useTitleABar;
  private String titleABarMsg;
  private boolean useActionAnnouncer;
  private List<String> actionMsg;
  private int actionTime;
  private boolean useChatMsg;
  private List<String> chatMsg;
  private boolean useSound;
  private String sound;
  private int volume;
  private int pitch;
  
  public IFOptions(InventoryFull i)
  {
    setUseSound(i.getConfig().getBoolean("sound_when_full.enabled"));
    setSound(i.getConfig().getString("sound_when_full.sound"));
    setVolume(i.getConfig().getInt("sound_when_full.volume"));
    setPitch(i.getConfig().getInt("sound_when_full.pitch"));
    
    setCooldownTime(i.getConfig().getInt("cooldown_time"));
    setMaxAlerts(i.getConfig().getInt("max_alerts_until_cooldown"));
    
    setUseHolo(i.getConfig().getBoolean("holographicdisplays.use_hologram"));
    setHoloMsg(i.getConfig().getStringList("holographicdisplays.message"));
    setHoloTime(i.getConfig().getInt("holographicdisplays.display_time"));
    
    setUseChatMsg(i.getConfig().getBoolean("chat_message.use_chat_message"));
    setChatMsg(i.getConfig().getStringList("chat_message.message"));
  }
  
  public int getCooldownTime()
  {
    return this.cooldownTime;
  }
  
  private void setCooldownTime(int cooldownTime)
  {
    this.cooldownTime = cooldownTime;
  }
  
  public int getMaxAlerts()
  {
    return this.maxAlerts;
  }
  
  private void setMaxAlerts(int maxAlerts)
  {
    this.maxAlerts = maxAlerts;
  }
  
  public boolean useHolo()
  {
    return this.useHolo;
  }
  
  private void setUseHolo(boolean useHolo)
  {
    this.useHolo = useHolo;
  }
  
  public List<String> getHoloMsg()
  {
    return this.holoMsg;
  }
  
  private void setHoloMsg(List<String> holoMsg)
  {
    this.holoMsg = holoMsg;
  }
  
  public int getHoloTime()
  {
    return this.holoTime;
  }
  
  private void setHoloTime(int holoTime)
  {
    this.holoTime = holoTime;
  }
  
  public boolean useTitleManager()
  {
    return this.useTitleManager;
  }
  
  private void setUseTitleManager(boolean useTitleManager)
  {
    this.useTitleManager = useTitleManager;
  }
  
  public String getTitleMsg()
  {
    return this.titleMsg;
  }
  
  private void setTitleMsg(String titleMsg)
  {
    this.titleMsg = titleMsg;
  }
  
  public String getSubTitleMsg()
  {
    return this.subTitleMsg;
  }
  
  private void setSubTitleMsg(String subTitleMsg)
  {
    this.subTitleMsg = subTitleMsg;
  }
  
  public int getFadeIn()
  {
    return this.fadeIn;
  }
  
  private void setFadeIn(int fadeIn)
  {
    this.fadeIn = fadeIn;
  }
  
  public int getFadeOut()
  {
    return this.fadeOut;
  }
  
  private void setFadeOut(int fadeOut)
  {
    this.fadeOut = fadeOut;
  }
  
  public int getDuration()
  {
    return this.duration;
  }
  
  private void setDuration(int duration)
  {
    this.duration = duration;
  }
  
  public boolean useTitleABar()
  {
    return this.useTitleABar;
  }
  
  private void setUseTitleABar(boolean useTitleABar)
  {
    this.useTitleABar = useTitleABar;
  }
  
  public String getTitleABarMsg()
  {
    return this.titleABarMsg;
  }
  
  private void setTitleABarMsg(String titleABarMsg)
  {
    this.titleABarMsg = titleABarMsg;
  }
  
  public boolean useActionAnnouncer()
  {
    return this.useActionAnnouncer;
  }
  
  private void setUseActionAnnouncer(boolean useActionAnnouncer)
  {
    this.useActionAnnouncer = useActionAnnouncer;
  }
  
  public List<String> getActionMsg()
  {
    return this.actionMsg;
  }
  
  private void setActionMsg(List<String> actionMsg)
  {
    this.actionMsg = actionMsg;
  }
  
  public boolean useChatMsg()
  {
    return this.useChatMsg;
  }
  
  private void setUseChatMsg(boolean useChatMsg)
  {
    this.useChatMsg = useChatMsg;
  }
  
  public List<String> getChatMsg()
  {
    return this.chatMsg;
  }
  
  private void setChatMsg(List<String> chatMsg)
  {
    this.chatMsg = chatMsg;
  }
  
  public int getActionTime()
  {
    return this.actionTime;
  }
  
  public void setActionTime(int actionTime)
  {
    this.actionTime = actionTime;
  }
  
  public boolean useSound()
  {
    return this.useSound;
  }
  
  public void setUseSound(boolean useSound)
  {
    this.useSound = useSound;
  }
  
  public String getSound()
  {
    return this.sound;
  }
  
  public void setSound(String sound)
  {
    this.sound = sound;
  }
  
  public int getVolume()
  {
    return this.volume;
  }
  
  public void setVolume(int volume)
  {
    this.volume = volume;
  }
  
  public int getPitch()
  {
    return this.pitch;
  }
  
  public void setPitch(int pitch)
  {
    this.pitch = pitch;
  }
}