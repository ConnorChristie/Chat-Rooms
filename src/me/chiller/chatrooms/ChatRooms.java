package me.chiller.chatrooms;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ChatRooms extends JavaPlugin
{
	public Logger log = Logger.getLogger("Minecraft");
	
	public CrListener crListener;
	public CrCommands crCommands;
	
	public Map<ChatChannel, String> oChannel = new HashMap<ChatChannel, String>();
	public Map<Player, ChatChannel> pChannel = new HashMap<Player, ChatChannel>();
	public Map<Player, ChatChannel> tChannel = new HashMap<Player, ChatChannel>();
	
	public void onEnable()
	{
		crListener = new CrListener(this);
		crCommands = new CrCommands(this);
		
		getConfig().addDefault("message-format", "§b[%chatroom%] §9%playername%§f: %message%");
		getConfig().options().copyDefaults(true);
		
		saveConfig();
		
		for (String ti : getConfig().getKeys(false))
		{
			if (!ti.equals("message-format"))
			{
				String ow = getConfig().getString(ti + ".ow");
				String wo = getConfig().getString(ti + ".wo");
				Vector st = getConfig().getVector(ti + ".st");
				Vector nd = getConfig().getVector(ti + ".nd");
				
				oChannel.put(new ChatChannel(ti, st.toLocation(Bukkit.getWorld(wo)), nd.toLocation(Bukkit.getWorld(wo))), ow);
			}
		}
		
		log.info("[ChatRooms] Loaded " + (getConfig().getKeys(false).size() - 1) + " Chat Room" + (getConfig().getKeys(false).size() - 1 == 1 ? "" : "s") + " from the config");
			
		Bukkit.getPluginManager().registerEvents(crListener, this);
			
		getCommand("chatrooms").setExecutor(crCommands);
		getCommand("cr").setExecutor(crCommands);
	}
	
	public void onDisable()
	{
		
	}
}