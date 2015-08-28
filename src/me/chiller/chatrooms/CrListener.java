package me.chiller.chatrooms;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CrListener implements Listener
{
	private ChatRooms cr;

	public Map<Player, Location> clicks = new HashMap<Player, Location>();

	public CrListener(ChatRooms plugin)
	{
		cr = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(final PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null)
		{
			if (event.getItem().getType().equals(Material.STICK) && clicks.containsKey(event.getPlayer()) && event.getPlayer().hasPermission("chatrooms.create"))
			{
				if (clicks.get(event.getPlayer()) == null)
				{
					event.getPlayer().sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "First point selected, now select the second");
					
					clicks.put(event.getPlayer(), event.getClickedBlock().getLocation());
				} else
				{
					Location fc = (Location) clicks.get(event.getPlayer());
					Location sc = event.getClickedBlock().getLocation();

					if (fc.getWorld().equals(sc.getWorld()))
					{
						cr.tChannel.put(event.getPlayer(), new ChatChannel("", fc, sc));
						
						event.getPlayer().sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "Second point selected, type /cr create <chatroom name>");
						clicks.remove(event.getPlayer());
					} else
					{
						event.getPlayer().sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "The second point you selected was not in the same world as the first point");
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		if (cr.pChannel.containsKey(event.getPlayer()))
		{
			for (Player pl : event.getRecipients())
			{
				if (cr.pChannel.containsKey(pl))
				{
					if (((ChatChannel) cr.pChannel.get(pl)).equals(cr.pChannel.get(event.getPlayer())))
					{
						pl.sendMessage(cr.getConfig().getString("message-format").replaceAll("%chatroom%", ((ChatChannel) cr.pChannel.get(event.getPlayer())).getTitle()).replaceAll("%playername%", event.getPlayer().getDisplayName()).replaceAll("%message%", event.getMessage()));
					}
				}
			}
		} else
		{
			for (Player pl : event.getRecipients())
			{
				if (!cr.pChannel.containsKey(pl))
				{
					pl.sendMessage(cr.getConfig().getString("message-format").replaceAll("%chatroom%", "Global").replaceAll("%playername%", event.getPlayer().getDisplayName()).replaceAll("%message%", event.getMessage()));
				}
			}
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		
		if (!player.hasPermission("chatrooms.nojoin"))
		{
			if (!cr.pChannel.containsKey(player))
			{
				for (ChatChannel cc : cr.oChannel.keySet())
				{
					if (cc.isLocInside(event.getTo()))
					{
						cr.pChannel.put(player, cc);
	
						player.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "You have joined the " + cc.getTitle() + " Chat Room");
					}
				}
			} else
			{
				ChatChannel cc = (ChatChannel) cr.pChannel.get(player);
				
				if (!cc.isLocInside(event.getTo()))
				{
					cr.pChannel.remove(player);
	
					player.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "You have left the " + cc.getTitle() + " Chat Room");
				}
			}
		} else if (cr.pChannel.containsKey(player))
		{
			ChatChannel cc = (ChatChannel) cr.pChannel.get(player);
			
			if (cc != null)
			{
				cr.pChannel.remove(player);
			}
		}
	}
}