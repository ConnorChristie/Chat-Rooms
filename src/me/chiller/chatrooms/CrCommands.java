package me.chiller.chatrooms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CrCommands implements CommandExecutor
{
	private ChatRooms cr;

	public CrCommands(ChatRooms plugin)
	{
		cr = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg)
	{
		if (arg.length >= 1)
		{
			if (arg[0].equalsIgnoreCase("reload"))
			{
				if (sender instanceof Player)
				{
					Player pl = (Player) sender;
					
					if (!pl.hasPermission("chatrooms.reload"))
					{
						pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You do not have permission for that command");
						
						return true;
					}
				}
				
				cr.reloadConfig();
				cr.oChannel.clear();

				for (String ti : cr.getConfig().getKeys(false))
				{
					if (!ti.equals("message-format"))
					{
						String ow = cr.getConfig().getString(ti + ".ow");
						String wo = cr.getConfig().getString(ti + ".wo");
						Vector st = cr.getConfig().getVector(ti + ".st");
						Vector nd = cr.getConfig().getVector(ti + ".nd");
	
						ChatChannel cc = new ChatChannel(ti, st.toLocation(Bukkit.getWorld(wo)), nd.toLocation(Bukkit.getWorld(wo)));
	
						cr.oChannel.put(cc, ow);
					}
				}

				if ((sender instanceof Player))
				{
					Player pl = (Player)sender;

					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "Successfully reloaded the config");
				} else
				{
					cr.log.info("[ChatRooms] Successfully reloaded the config");
				}
				
				cr.log.info("[ChatRooms] Loaded " + (cr.getConfig().getKeys(false).size() - 1) + " Chat Room" + (cr.getConfig().getKeys(false).size() - 1 == 1 ? "" : "s") + " from the config");

				return true;
			}
		}

		if (sender instanceof Player)
		{
			Player pl = (Player) sender;

			if (arg.length >= 1)
			{
				StringBuilder sb;
				
				if (arg[0].equalsIgnoreCase("create") && pl.hasPermission("chatrooms.create"))
				{
					if (arg.length >= 2)
					{
						if (cr.tChannel.containsKey(pl))
						{
							ChatChannel cc = (ChatChannel) cr.tChannel.get(pl);

							String ti = "";

							for (int i = 1; i < arg.length; i++)
							{
								if (i == 1)
								{
									ti = ti + arg[i];
								} else
								{
									ti = ti + " " + arg[i];
								}
							}
							
							String[] spl = ti.split("");
							sb = new StringBuilder(25);
							
							for (int i = 0; i < 26; i++)
							{
								if (i < spl.length)
								{
									sb.append(spl[i]);
								}
							}

							ti = sb.toString();
							
							cr.oChannel.put(cc.setTitle(ti), pl.getName());
							cr.tChannel.remove(cc);

							pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "Successfully created the Chat Room: " + ti);
							
							cr.getConfig().set(cc.getTitle() + ".ow", pl.getName());
							cr.getConfig().set(cc.getTitle() + ".wo", cc.getSt().getWorld().getName());
							cr.getConfig().set(cc.getTitle() + ".st", cc.getSt().toVector());
							cr.getConfig().set(cc.getTitle() + ".nd", cc.getNd().toVector());
							cr.saveConfig();

							return true;
						}

						pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You need to first do: /" + label + " tool");

						return true;
					}

					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "No title specified, usage: /" + label + " create <chatroom name>");

					return true;
				} else if (arg[0].equalsIgnoreCase("create"))
				{
					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You do not have permission for that command");
					
					return true;
				}

				if (arg[0].equalsIgnoreCase("tool") && pl.hasPermission("chatrooms.create"))
				{
					cr.crListener.clicks.put(pl, null);
					
					pl.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STICK, 1) });
					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "Right click the first point of the Chat Room cuboid with this stick");

					return true;
				} else if (arg[0].equalsIgnoreCase("tool"))
				{
					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You do not have permission for that command");
					
					return true;
				}
				
				if (arg[0].equalsIgnoreCase("remove") && pl.hasPermission("chatrooms.remove"))
				{
					if (arg.length >= 2)
					{
						String ti = "";

						for (int i = 1; i < arg.length; i++)
						{
							if (i == 1)
							{
								ti = ti + arg[i];
							} else
							{
								ti = ti + " " + arg[i];
							}
						}

						for (ChatChannel cc : cr.oChannel.keySet())
						{
							if (cc.getTitle().equalsIgnoreCase(ti))
							{
								cr.oChannel.remove(cc);
								cr.getConfig().set(cc.getTitle(), null);
								cr.saveConfig();

								pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.GOLD + "Successfully removed the Chat Room: " + ti);

								return true;
							}
						}

						pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "Could not find the Chat Room: " + ti);

						return true;
					}

					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "No title param specified, usage: /" + label + " remove <chatroom name>");

					return true;
				} else if (arg[0].equalsIgnoreCase("remove"))
				{
					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You do not have permission for that command");
					
					return true;
				}

				if (arg[0].equalsIgnoreCase("list") && pl.hasPermission("chatrooms.list"))
				{
					pl.sendMessage(ChatColor.AQUA + "Chat Rooms " + ChatColor.GOLD + "(total: " + cr.oChannel.size() + ") list:");

					if (this.cr.oChannel.size() == 0)
					{
						pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + "There are no Chat Rooms");
					} else
					{
						for (ChatChannel cc : cr.oChannel.keySet())
						{
							pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + cc.getTitle() + ChatColor.RED + ", Owner: " + ChatColor.GOLD + (String) cr.oChannel.get(cc));
						}
					}

					return true;
				} else if (arg[0].equalsIgnoreCase("list"))
				{
					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You do not have permission for that command");
					
					return true;
				}

				if (arg[0].equalsIgnoreCase("help") && pl.hasPermission("chatrooms.help"))
				{
					pl.sendMessage(ChatColor.AQUA + "Chat Rooms " + ChatColor.GOLD + " help:");

					pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + "/cr tool" + ChatColor.RED + " - " + ChatColor.GOLD + "Gives you the tool to select your Chat Room");
					pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + "/cr create <chatroom name>" + ChatColor.RED + " - " + ChatColor.GOLD + "Creates that Chat Room");
					pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + "/cr remove <chatroom name>" + ChatColor.RED + " - " + ChatColor.GOLD + "Deletes that Chat Room");
					pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + "/cr list" + ChatColor.RED + " - " + ChatColor.GOLD + "Lists all of the Chat Rooms");
					pl.sendMessage(ChatColor.AQUA + "-- " + ChatColor.GOLD + "/cr reload" + ChatColor.RED + " - " + ChatColor.GOLD + "Reloads the config");

					return true;
				} else if (arg[0].equalsIgnoreCase("help"))
				{
					pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "You do not have permission for that command");
					
					return true;
				}

				pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "No such command: " + arg[0]);

				return true;
			}

			pl.sendMessage(ChatColor.AQUA + "[ChatRooms] " + ChatColor.RED + "Command usage: /" + label + " <command> [params]");

			return true;
		}

		return true;
	}
}