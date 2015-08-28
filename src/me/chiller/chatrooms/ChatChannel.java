package me.chiller.chatrooms;

import org.bukkit.Location;

public class ChatChannel
{
	private String ti = "";
	private Location st = null;
	private Location nd = null;

	public ChatChannel(String ti, Location st, Location nd)
	{
		this.ti = ti;

		int stX = Math.min(st.getBlockX(), nd.getBlockX());
		int stY = Math.min(st.getBlockY(), nd.getBlockY());
		int stZ = Math.min(st.getBlockZ(), nd.getBlockZ());

		int ndX = Math.max(st.getBlockX(), nd.getBlockX());
		int ndY = Math.max(st.getBlockY(), nd.getBlockY());
		int ndZ = Math.max(st.getBlockZ(), nd.getBlockZ());

		this.st = new Location(st.getWorld(), stX, stY, stZ);
		this.nd = new Location(nd.getWorld(), ndX, ndY, ndZ);
	}
	
	public String getTitle()
	{
		return this.ti;
	}

	public ChatChannel setTitle(String title)
	{
		this.ti = title;

		return this;
	}

	public Location getSt()
	{
		return this.st;
	}

	public Location getNd()
	{
		return this.nd;
	}

	public boolean isLocInside(Location l)
	{
		double pX = l.getX();
		double pY = l.getY();
		double pZ = l.getZ();

		if ((l.getWorld().equals(this.st.getWorld())) && (l.getWorld().equals(this.nd.getWorld())) && (pX >= this.st.getBlockX()) && (pX <= this.nd.getBlockX()) && (pY >= this.st.getBlockY()) && (pY <= this.nd.getBlockY()) && (pZ >= this.st.getBlockZ()) && (pZ <= this.nd.getBlockZ()))
		{
			return true;
		}

		return false;
	}
}