package me.egg82.tcpp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.egg82.tcpp.enums.CommandErrorType;
import me.egg82.tcpp.enums.MessageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.services.SpinRegistry;
import me.egg82.tcpp.services.SpinSpeedRegistry;
import me.egg82.tcpp.util.MetricsHelper;
import ninja.egg82.events.CommandEvent;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.enums.SpigotCommandErrorType;
import ninja.egg82.plugin.enums.SpigotMessageType;
import ninja.egg82.plugin.utils.CommandUtil;

public class SpinCommand extends PluginCommand {
	//vars
	private IRegistry spinRegistry = (IRegistry) ServiceLocator.getService(SpinRegistry.class);
	private IRegistry spinSpeedRegistry = (IRegistry) ServiceLocator.getService(SpinSpeedRegistry.class);
	
	private MetricsHelper metricsHelper = (MetricsHelper) ServiceLocator.getService(MetricsHelper.class);
	
	//constructor
	public SpinCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (!CommandUtil.hasPermission(sender, PermissionsType.COMMAND_SPIN)) {
			sender.sendMessage(SpigotMessageType.NO_PERMISSIONS);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.NO_PERMISSIONS);
			return;
		}
		if (!CommandUtil.isArrayOfAllowedLength(args, 1, 2)) {
			sender.sendMessage(SpigotMessageType.INCORRECT_USAGE);
			sender.getServer().dispatchCommand(sender, "help " + command.getName());
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.INCORRECT_USAGE);
			return;
		}
		
		Player player = CommandUtil.getPlayerByName(args[0]);
		
		if (player == null) {
			sender.sendMessage(SpigotMessageType.PLAYER_NOT_FOUND);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.PLAYER_NOT_FOUND);
			return;
		}
		if (CommandUtil.hasPermission(player, PermissionsType.IMMUNE)) {
			sender.sendMessage(MessageType.PLAYER_IMMUNE);
			dispatch(CommandEvent.ERROR, CommandErrorType.PLAYER_IMMUNE);
			return;
		}
		
		String uuid = player.getUniqueId().toString();
		
		float speed = -11.25f;
		if (args.length == 2) {
			try {
				speed = Float.parseFloat(args[1]);
			} catch (Exception ex) {
				sender.sendMessage(SpigotMessageType.INCORRECT_USAGE);
				sender.getServer().dispatchCommand(sender, "help " + command.getName());
				dispatch(CommandEvent.ERROR, SpigotCommandErrorType.INCORRECT_USAGE);
				return;
			}
		}
		
		if (!spinRegistry.hasRegister(uuid)) {
			e(uuid, player, speed);
		} else {
			eUndo(uuid, player);
		}
		
		dispatch(CommandEvent.COMPLETE, null);
	}
	private void e(String uuid, Player player, float speed) {
		spinRegistry.setRegister(uuid, Player.class, player);
		spinSpeedRegistry.setRegister(uuid, Float.class, speed);
		metricsHelper.commandWasRun(command.getName());
		
		sender.sendMessage(player.getName() + " is now spinning in circles.");
	}
	
	protected void onUndo() {
		Player player = CommandUtil.getPlayerByName(args[0]);
		String uuid = player.getUniqueId().toString();
		
		if (spinRegistry.hasRegister(uuid)) {
			eUndo(uuid, player);
		}
		
		dispatch(CommandEvent.COMPLETE, null);
	}
	private void eUndo(String uuid, Player player) {
		spinRegistry.setRegister(uuid, Player.class, null);
		spinSpeedRegistry.setRegister(uuid, Float.class, null);
		
		sender.sendMessage(player.getName() + " is no longer spinning in circles.");
	}
}
