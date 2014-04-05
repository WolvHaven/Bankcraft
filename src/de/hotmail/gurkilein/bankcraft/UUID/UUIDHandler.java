package de.hotmail.gurkilein.bankcraft.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.Bankcraft;

public class UUIDHandler {

	private Bankcraft bankcraft;

	public UUIDHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	
	public UUID getUUID(String name) {
		try {
			return getUUIDwE(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public UUID getUUIDwE(String name) throws Exception {
		Player p = null;
		if ((p = bankcraft.getServer().getPlayer(name)) != null) {
			return p.getUniqueId();
		} else {
			List <String> uL = new ArrayList <String> ();
			uL.add(name);
			UUIDFetcher fetcher = new UUIDFetcher(uL);
			Map<String, UUID> response = null;
			response = fetcher.call();
			return response.get(name);
		}
	}
	
	public String getName(UUID uuid) {
		Player p = null;
		if ((p = bankcraft.getServer().getPlayer(uuid)) != null) {
			return p.getName();
		} else {
			List <UUID> uL = new ArrayList <UUID> ();
			uL.add(uuid);
			NameFetcher fetcher = new NameFetcher(uL);
			Map<UUID, String> response = null;
			try {
			response = fetcher.call();
			} catch (Exception e) {
			e.printStackTrace();
			}
			return response.get(uuid);
		}
	}

}
