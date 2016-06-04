package me.egg82.tcpp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.egg82.events.patterns.command.CommandEvent;
import com.egg82.patterns.ServiceLocator;
import com.egg82.plugin.commands.PluginCommand;
import com.egg82.registry.interfaces.IRegistry;

import me.egg82.tcpp.enums.CommandErrorType;
import me.egg82.tcpp.enums.MessageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.enums.PluginServiceType;

public class NauseaCommand extends PluginCommand {
	//vars
	IRegistry nauseaRegistry = (IRegistry) ServiceLocator.getService(PluginServiceType.NAUSEA_REGISTRY);
	
	//constructor
	public NauseaCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	
	//private
	protected void execute() {
		if (sender instanceof Player && !permissionsManager.playerHasPermission((Player) sender, PermissionsType.COMMAND_NAUSEA)) {
			sender.sendMessage(MessageType.NO_PERMISSIONS);
			dispatch(CommandEvent.ERROR, CommandErrorType.NO_PERMISSIONS);
			return;
		}
		
		if (args.length == 1) {
			nausea(args[0], Bukkit.getPlayer(args[0]));
		} else {
			sender.sendMessage(MessageType.INCORRECT_USAGE);
			sender.getServer().dispatchCommand(sender, "help " + command.getName());
			dispatch(CommandEvent.ERROR, CommandErrorType.INCORRECT_USAGE);
		}
	}
	private void nausea(String name, Player player) {
		if (player == null) {
			sender.sendMessage(MessageType.PLAYER_NOT_FOUND);
			dispatch(CommandEvent.ERROR, CommandErrorType.PLAYER_NOT_FOUND);
			return;
		}
		if (permissionsManager.playerHasPermission(player, PermissionsType.IMMUNE)) {
			sender.sendMessage(MessageType.PLAYER_IMMUNE);
			dispatch(CommandEvent.ERROR, CommandErrorType.PLAYER_IMMUNE);
			return;
		}
		
		if (nauseaRegistry.contains(name.toLowerCase())) {
			sender.sendMessage(name + " is no longer nauseous.");
			nauseaRegistry.setRegister(name.toLowerCase(), null);
		} else {
			sender.sendMessage(name + " is now nauseous.");
			nauseaRegistry.setRegister(name.toLowerCase(), player);
		}
		
		dispatch(CommandEvent.COMPLETE, null);
	}
}
