package me.egg82.tcpp.events.player.playerJoin;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

import me.egg82.tcpp.services.ElectrifyRegistry;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;

public class ElectrifyEventCommand extends EventCommand {
	//vars
	IRegistry electrifyRegistry = (IRegistry) ServiceLocator.getService(ElectrifyRegistry.class);
	
	//constructor
	public ElectrifyEventCommand(Event event) {
		super(event);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		PlayerJoinEvent e = (PlayerJoinEvent) event;
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
		
		if (electrifyRegistry.hasRegister(uuid)) {
			electrifyRegistry.setRegister(uuid, Player.class, player);
		}
	}
}