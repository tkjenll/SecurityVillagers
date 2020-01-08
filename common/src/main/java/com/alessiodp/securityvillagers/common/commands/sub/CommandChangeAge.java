package com.alessiodp.securityvillagers.common.commands.sub;

import com.alessiodp.core.common.ADPPlugin;
import com.alessiodp.core.common.commands.utils.ADPMainCommand;
import com.alessiodp.core.common.commands.utils.ADPSubCommand;
import com.alessiodp.core.common.commands.utils.CommandData;
import com.alessiodp.core.common.user.User;
import com.alessiodp.securityvillagers.common.SecurityVillagersPlugin;
import com.alessiodp.securityvillagers.common.commands.utils.SecurityVillagersPermission;
import com.alessiodp.securityvillagers.common.configuration.SVConstants;
import com.alessiodp.securityvillagers.common.configuration.data.ConfigMain;
import com.alessiodp.securityvillagers.common.configuration.data.Messages;
import com.alessiodp.securityvillagers.common.tasks.ChangeAgeCooldown;
import com.alessiodp.securityvillagers.common.tasks.ProfessionCooldown;
import com.alessiodp.securityvillagers.common.utils.SVPlayerUtils;
import com.alessiodp.securityvillagers.common.villagers.objects.ProtectedEntity;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandChangeAge extends ADPSubCommand {
	@Getter private final boolean executableByConsole = false;
	
	public CommandChangeAge(ADPPlugin plugin, ADPMainCommand mainCommand) {
		super(plugin, mainCommand);
	}
	
	@Override
	public boolean preRequisites(CommandData commandData) {
		User sender = commandData.getSender();
		
		if (!sender.hasPermission(SecurityVillagersPermission.ADMIN_CHANGEAGE.toString())) {
			((SVPlayerUtils) plugin.getPlayerUtils()).sendNoPermissionMessage(sender, SecurityVillagersPermission.ADMIN_CHANGEAGE);
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onCommand(CommandData commandData) {
		User player = commandData.getSender();
		
		plugin.getLoggerManager().logDebug(SVConstants.DEBUG_CMD_CHANGEAGE
				.replace("{player}", player.getName())
				.replace("{value}", commandData.getArgs().length > 1 ? commandData.getArgs()[1] : ""), true);
		
		// Command handling
		ProtectedEntity protectedEntity = ((SecurityVillagersPlugin) plugin).getVillagerManager().getSelectedEntities().get(player.getUUID());
		if (protectedEntity == null) {
			player.sendMessage(Messages.GENERAL_SELECTION_REQUIRED, true);
			return;
		}
		
		if (!protectedEntity.isAgeable()) {
			player.sendMessage(Messages.CMD_CHANGEAGE_FAILED, true);
			return;
		}
		
		if (ConfigMain.CHANGEAGE_COOLDOWN > 0
				&& !player.hasPermission(SecurityVillagersPermission.ADMIN_CHANGEAGE_CD_BYPASS.toString())) {
			Long unixTimestamp = ((SecurityVillagersPlugin) plugin).getChangeAgeCooldown().get(player.getUUID());
			long unixNow = System.currentTimeMillis() / 1000L;
			// Check cooldown
			if (unixTimestamp != null && (unixNow - unixTimestamp) < ConfigMain.CHANGEAGE_COOLDOWN) {
				player.sendMessage(Messages.CMD_CHANGEAGE_COOLDOWN
						.replace("%seconds%", String.valueOf(ConfigMain.CHANGEAGE_COOLDOWN - (unixNow - unixTimestamp))), true);
				return;
			}
			
			((SecurityVillagersPlugin) plugin).getChangeAgeCooldown().put(player.getUUID(), unixNow);
			plugin.getScheduler().scheduleAsyncLater(new ChangeAgeCooldown((SecurityVillagersPlugin) plugin, player.getUUID()), ConfigMain.CHANGEAGE_COOLDOWN, TimeUnit.SECONDS);
			
			plugin.getLoggerManager().logDebug(SVConstants.DEBUG_CMD_CHANGEAGE_TASK
					.replace("{value}", Integer.toString(ConfigMain.CHANGEAGE_COOLDOWN))
					.replace("{player}", player.getName()), true);
		}
		
		// Command starts
		if (protectedEntity.isAdult()) {
			protectedEntity.setToBaby();
			player.sendMessage(Messages.CMD_CHANGEAGE_BABY, true);
		} else {
			protectedEntity.setToAdult();
			player.sendMessage(Messages.CMD_CHANGEAGE_ADULT, true);
		}
	}
	
	@Override
	public List<String> onTabComplete(@NonNull User sender, String[] args) {
		return null;
	}
}
