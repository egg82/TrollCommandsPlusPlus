package me.egg82.tcpp.events.player.playerJoin;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import me.egg82.tcpp.services.NightmareRegistry;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.utils.TaskUtil;
import ninja.egg82.protocol.core.IFakeLivingEntity;

public class NightmareEventCommand extends EventCommand<PlayerJoinEvent> {
	//vars
	private IRegistry<UUID> nightmareRegistry = ServiceLocator.getService(NightmareRegistry.class);
	
	//constructor
	public NightmareEventCommand(PlayerJoinEvent event) {
		super(event);
	}
	
	//public
	
	//private
	@SuppressWarnings("unchecked")
	protected void onExecute(long elapsedMilliseconds) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (nightmareRegistry.hasRegister(uuid)) {
			TaskUtil.runAsync(new Runnable() {
				public void run() {
					Collection<IFakeLivingEntity> entities = nightmareRegistry.getRegister(uuid, Collection.class);
					for (IFakeLivingEntity e : entities) {
						e.addPlayer(player);
					}
				}
			}, 80L);
		}
	}
}