package de.hotmail.gurkilein.bankcraft.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.Bankcraft;

public class UUIDHandler {

	private Map<String,UUID> uuidMap = new ConcurrentHashMap <String,UUID>();
	private Map<UUID,String> nameMap = new ConcurrentHashMap <UUID,String>();
	
	private Bankcraft bankcraft;

	public UUIDHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	
	public UUID getUUID(String name) {
		return getUUID(name, null);
	}
	
	public UUID getUUID(String name, Player observer) {
		try {
			return getUUIDwE(name, observer);
		} catch (Exception e) {
			Bankcraft.log.severe("Could not retrieve UUID for '"+name+"'! Make sure that the server has access to the internet!");
			e.printStackTrace();
		}
		return null;
	}
	
	public UUID getUUIDwE(String name) throws Exception {
		return getUUIDwE(name, null);
	}
	
	@SuppressWarnings("deprecation")
	public UUID getUUIDwE(String name, Player observer) throws Exception {
		Player p = null;
		if (observer != null && observer.getName().equals(name)) {
			nameMap.put(observer.getUniqueId(), name);
			uuidMap.put(name, observer.getUniqueId());
			return observer.getUniqueId();
		} else if ((p = bankcraft.getServer().getPlayer(name)) != null) {
			nameMap.put(p.getUniqueId(), name);
			uuidMap.put(name, p.getUniqueId());
			return p.getUniqueId();
		} else if (uuidMap.containsKey(name)) {
			return uuidMap.get(name);
		} else {
			if (observer != null) bankcraft.getConfigurationHandler().printMessage(observer, "message.performingLookup", "", null, null);
			List <String> uL = new ArrayList <String> ();
			uL.add(name);
			UUIDFetcher fetcher = new UUIDFetcher(uL);
			Map<String, UUID> response = null;
			response = fetcher.call();
			nameMap.put(response.get(name), name);
			uuidMap.put(name, response.get(name));
			return response.get(name);
		}
	}
	
	public String getName(UUID uuid) {
		Player p = null;
		if ((p = bankcraft.getServer().getPlayer(uuid)) != null) {
			nameMap.put(uuid, p.getName());
			uuidMap.put(p.getName(), uuid);
			return p.getName();
		} else if (nameMap.containsKey(uuid)) {
			return nameMap.get(uuid);
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
			nameMap.put(uuid, response.get(uuid));
			uuidMap.put(response.get(uuid), uuid);
			return response.get(uuid);
		}
	}

}
