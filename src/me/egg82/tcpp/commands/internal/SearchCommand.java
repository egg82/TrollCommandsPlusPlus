package me.egg82.tcpp.commands.internal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.egg82.tcpp.enums.CommandErrorType;
import me.egg82.tcpp.enums.MessageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.services.TrollInventoryRegistry;
import me.egg82.tcpp.services.TrollPageRegistry;
import me.egg82.tcpp.services.TrollPlayerRegistry;
import me.egg82.tcpp.services.TrollSearchRegistry;
import me.egg82.tcpp.util.GuiUtil;
import me.egg82.tcpp.util.MetricsHelper;
import ninja.egg82.events.CommandEvent;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.enums.SpigotCommandErrorType;
import ninja.egg82.plugin.enums.SpigotMessageType;
import ninja.egg82.plugin.utils.CommandUtil;

public class SearchCommand extends PluginCommand {
	//vars
	private IRegistry trollInventoryRegistry = (IRegistry) ServiceLocator.getService(TrollInventoryRegistry.class);
	private IRegistry trollPlayerRegistry = (IRegistry) ServiceLocator.getService(TrollPlayerRegistry.class);
	private IRegistry trollPageRegistry = (IRegistry) ServiceLocator.getService(TrollPageRegistry.class);
	private IRegistry trollSearchRegistry = (IRegistry) ServiceLocator.getService(TrollSearchRegistry.class);
	
	private MetricsHelper metricsHelper = (MetricsHelper) ServiceLocator.getService(MetricsHelper.class);
	
	//constructor
	public SearchCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (!CommandUtil.hasPermission(sender, PermissionsType.COMMAND_SEARCH)) {
			sender.sendMessage(SpigotMessageType.NO_PERMISSIONS);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.NO_PERMISSIONS);
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(SpigotMessageType.INCORRECT_USAGE);
			String name = getClass().getSimpleName();
			name = name.substring(0, name.length() - 7).toLowerCase();
			sender.getServer().dispatchCommand(sender, "troll help " + name);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.INCORRECT_USAGE);
			return;
		}
		if (!CommandUtil.isPlayer(sender)) {
			sender.sendMessage(SpigotMessageType.CONSOLE_NOT_ALLOWED);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.CONSOLE_NOT_ALLOWED);
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
		
		String search = "";
		if (args.length >= 2) {
			for (int i = 1; i < args.length; i++) {
				search += args[i] + " ";
			}
			search = search.trim();
		}
		
		e(player.getUniqueId().toString(), player, ((Player) sender).getUniqueId().toString(), (Player) sender, search);
		
		dispatch(CommandEvent.COMPLETE, null);
	}
	private void e(String uuid, Player player, String senderUuid, Player senderPlayer, String search) {
		Inventory inv = GuiUtil.createInventory(senderPlayer, search, 0);
		trollInventoryRegistry.setRegister(senderUuid, Inventory.class, inv);
		trollPlayerRegistry.setRegister(senderUuid, Player.class, player);
		trollPageRegistry.setRegister(senderUuid, Integer.class, 0);
		trollSearchRegistry.setRegister(senderUuid, String.class, search);
		
		senderPlayer.openInventory(inv);
		
		metricsHelper.commandWasRun(this);
	}
	
	protected void onUndo() {
		
	}
}