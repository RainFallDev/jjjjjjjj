package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "skywars cooldown", permission = "practice.skywars.cooldown")
public class SkyWarsCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getSkyWarsManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a SkyWars Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the SkyWars Event cooldown.");

		Array.get().getSkyWarsManager().setCooldown(new Cooldown(0));
	}

}
