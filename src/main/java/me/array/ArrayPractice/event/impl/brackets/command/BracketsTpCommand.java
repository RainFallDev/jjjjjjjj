package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets tp", permission = "practice.brackets.tp")
public class BracketsTpCommand {

	public void execute(Player player) {
		player.teleport(Array.get().getBracketsManager().getBracketsSpectator());
		player.sendMessage(CC.GREEN + "Teleported to brackets's spawn location.");
	}

}
